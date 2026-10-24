# Assignment 1: Warehouse Order Processor

## Scenario
A small online shop keeps an inventory of products. Orders come in throughout the day. Write a Java program that processes a batch of orders against the inventory and reports the results.

## Inputs
**Inventory.** Each product has:
- SKU (unique string ID, e.g. `"A100"`)
- name
- unit price
- quantity in stock

**Orders.** Each order has:
- order ID
- one or more line items, each a SKU plus a quantity

Hard-coding the sample data in `main` is fine for the core assignment.

## Requirements
1. Process orders **in the order they arrive**.
2. An order is **all-or-nothing**. If any line item can't be fulfilled, reject the whole order and leave inventory unchanged.
3. Reject an order if it:
   - references a SKU that doesn't exist
   - asks for more than is in stock
   - has an invalid quantity (zero or negative)
4. For each order, print whether it was **fulfilled** or **rejected**. For rejections, include the reason.
5. For each fulfilled order, print its total cost.
6. At the end, print:
   - total revenue from fulfilled orders
   - a **low-stock report**: every product with fewer than 5 units left, sorted by quantity (lowest first)

## Sample data
| SKU  | Name     | Price  | Qty |
|------|----------|--------|-----|
| A100 | Keyboard | 49.99  | 10  |
| B200 | Mouse    | 19.99  | 25  |
| C300 | Monitor  | 189.00 | 4   |
| D400 | USB Cable| 5.49   | 50  |

| Order | Line items                     |
|-------|--------------------------------|
| 1     | A100 x2, B200 x2               |
| 2     | C300 x3                        |
| 3     | C300 x2, D400 x5               |
| 4     | Z999 x1                        |
| 5     | A100 x8, B200 x0               |
| 6     | A100 x8, D400 x10              |

Before you code, work out by hand what *should* happen to each order. That's your test oracle.

## Stretch goals (optional)
- Read inventory and orders from CSV files instead of hard-coding them.
- Write JUnit tests for the processing logic.
- Add a discount rule, e.g. 10% off any line item with quantity ≥ 10.

## How we'll work
Start by describing your design: what classes you'd create, what each one is responsible for, and which data structures you'd use. I'll review it before you write code.
