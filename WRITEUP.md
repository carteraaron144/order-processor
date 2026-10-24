# Assignment 1: Warehouse Order Processor (write-up)

**Status:** paused on 2026-10-23. Core processing works and was verified against the sample orders. Several requirements are still open (see the end).

## What it is
A Java 25 program that processes pending orders from a PostgreSQL database. Each order is all-or-nothing: if any line can't be filled, nothing changes. At the end it prints a low-stock report, each processed order, and total revenue.

**Verified result** (sample orders 1, 2, 3, 6, plus a rollback test order and a simulated poison order):

| Check | Result |
|---|---|
| Orders 1, 2, 6 | FULFILLED |
| Order 3 (only 1 monitor left) | REJECTED |
| An order whose first line passes and second fails | REJECTED, first line's stock rolled back |
| An order that always crashes | FAILED after 3 attempts, error saved, batch kept going |
| Total revenue | 1161.78 |
| Low stock | A100 0, C300 1 |

## How to run
```bash
brew services run postgresql@18
psql -d order_processor -f schema.sql && psql -d order_processor -f seed.sql   # wipes and reseeds
mvn -q compile
java -cp "target/classes:$(mvn -q dependency:build-classpath -Dmdep.outputFile=/dev/stdout)" orders.Main
```
There's no way to add orders from Java yet (`postNewOrder` is a stub), so orders have to be inserted with SQL first.

## Design decisions
- **The database is the queue.** Orders arrive as `PENDING`. The processor takes the oldest one (`ORDER BY order_time, order_id`). An order that doesn't finish stays `PENDING` and gets retried. This replaced the original staging table, which turned out to be redundant.
- **One transaction per order.** Stock decrements and the status change commit together or roll back together. All classes share one `DBHelper`, and so one `Connection`, so the transaction covers both managers.
- **Race-safe stock decrement.** `UPDATE ... SET qty = qty - ? WHERE item_id = ? AND qty >= ?` checks and subtracts in one statement, and its row count says whether it worked. A `CHECK (quantity_in_stock >= 0)` constraint is the backstop.
- **Validate as early as possible.** Unknown items and zero quantities are meant to be rejected at intake, with foreign keys and `CHECK` constraints enforcing it. Only stock, which changes over time, is checked during processing.
- **Prices are captured at sale.** `order_lines.unit_price` stores the price paid. `NUMERIC` in SQL and `BigDecimal` in Java, never `double`.
- **Poison orders.** If processing an order crashes, the processor rolls back, then tries to record the failed attempt. If that write works, the order was the problem: it gets up to 3 attempts, then `FAILED`. If the write also fails, the database is down: stop, and everything stays `PENDING`.
- **`DBHelper.query(sql, RowMapper<T>, Object... params)`.** One generic, parameterized query method. Callers pass a lambda that turns a row into an object.

## Main pain points
1. **Exception handling around transactions.** This was the hardest part. Specific gaps:
   - which exception type actually arrives (`DBHelper` wraps `SQLException` in `RuntimeException`)
   - a `catch` block doesn't catch exceptions thrown from a sibling `catch`
   - swallowing an exception after rolling back
   - `setAutoCommit(true)` silently *commits* an open transaction

   A dedicated primer is planned for a future assignment.
2. **Lambdas and generics.** Getting from "pass in a function" to `RowMapper<T>` took several steps: what `T` is, what a functional interface is, varargs, and why `Function` doesn't work with checked exceptions.
3. **Java ↔ SQL type mismatches.**
   - Postgres enums need `?::order_status` and a `String`.
   - Passing `Integer.toString(id)` to an `int` column fails.
   - Unquoted `PENDING` is read as a column name.
   - `ORDER BY a AND b` isn't `ORDER BY a, b`.
4. **Immutability.** `BigDecimal.add()` and `String.concat()` return new values, so calling them without assigning the result does nothing. This caused two separate bugs.
5. **Check-then-act races.** Reading stock in Java and then writing it back is unsafe with two processors. The fix was moving the check into the `UPDATE` itself.

## What's left before it's done
- **The failure reason is never saved or printed.** Nothing writes to `failed_orders`, and the reason doesn't reach the reports. This is a stated requirement.
- **The per-order output should be a summary:** "Order 3: REJECTED (INSUFFICIENT_STOCK)" or "Order 1: FULFILLED, $139.96". It currently dumps every field.
- **`postNewOrder` and the intake checks.** Insert the order and its lines in one transaction (`INSERT ... RETURNING order_id`), and reject unknown SKUs and quantities of 0 or less before inserting.
- **JUnit tests**, for `validateStockLine` first since it's pure, then the sample-order scenarios.
- **Small cleanups:**
  - extra blank lines in the low-stock header
  - unused imports, and the leftover `SetupCheckTest`
  - the `DBHelper` connection is never closed
  - DB credentials are hard-coded
- **Optional:** wait longer between retries (exponential backoff, using a `next_attempt_at` column); the stretch goals in the README (CSV input, discounts).
