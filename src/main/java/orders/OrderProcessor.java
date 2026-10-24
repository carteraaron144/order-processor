package orders;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class OrderProcessor {
    StockManager stockManager;
    OrderManager orderManager;
    DBHelper db;

    OrderProcessor(
            StockManager stockManager,
            OrderManager orderManager,
            DBHelper db
    ){
        this.stockManager = stockManager;
        this.orderManager = orderManager;
        this.db = db;
    }

    // How many times processing may crash on one order before it's marked FAILED.
    private static final int MAX_ATTEMPTS = 3;

    Optional<Order> processNextOrder() {
        // Declared outside the try so the catch knows which order failed.
        Order order = null;
        try {
            db.setAutoCommit(false);
            Optional<Order> nextOrder = orderManager.getNextOrder();

            if (nextOrder.isEmpty()) {
                // getNextOrder came back empty
                return nextOrder;
            } else {
                order = nextOrder.get();

                //check stock against requested qty
                Optional<FailReason> invalid = checkOrderValidity(order);

                //reject or fulfill based on validity
                if (invalid.isPresent()) {
                    try {
                        db.rollback();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    order.setStatus(OrderStatus.REJECTED);
                    orderManager.setOrderStatus(order, OrderStatus.REJECTED);
                } else {
                    order.setStatus(OrderStatus.FULFILLED);
                    orderManager.setOrderStatus(order, OrderStatus.FULFILLED);
                }

                db.commit();
                return nextOrder;
            }

        } catch (Exception e) {
            // Anything went wrong: undo the whole transaction...
            try {
                db.rollback();
            } catch (SQLException rollbackError) {
                e.addSuppressed(rollbackError);   // keep the original error, attach this one to it
            }

            if (order == null) {
                // Failed before we even had an order: the database itself is the problem. Stop.
                throw new RuntimeException("Could not fetch the next order", e);
            }

            // ...then try to record the failure. If that write works, the database is healthy and
            // this order is the problem (a "poison" order): count the attempt and let the caller move on.
            // If it fails too, the database is probably down: stop, and everything stays PENDING.
            try {
                db.setAutoCommit(true);   // the attempt record should commit on its own
                order.setStatus(orderManager.recordFailedAttempt(order, MAX_ATTEMPTS, e.toString()));
                return Optional.of(order);
            } catch (Exception recordError) {
                e.addSuppressed(recordError);
                throw new RuntimeException(
                        "Order " + order.getID() + " failed and the failure couldn't be recorded; stopping", e);
            }
        } finally {
            try {
                db.setAutoCommit(true);
            } catch (SQLException f) {
                throw new RuntimeException(f);
            }
        }
    }

    private Optional<FailReason> checkOrderValidity(Order order) {
        List<OrderLine> lines = order.getItems();

        //loop through lines in the order
        for (OrderLine line : lines) {


            int stock = stockManager.getStock(line.getItemId());

            //if we make it here, the item exists
            Optional<FailReason> v = validateStockLine(line, stock);

            if (v.isPresent()) {
                return v;
            }

            //if we make it here, the quantity was valid and there was sufficient stock
            int succ = stockManager.decrementStock(line.getItemId(), line.getQuantity());
            if (succ == 0) {
                // The conditional UPDATE refused: stock dropped below qty after we checked,
                // most likely because another processor took it first.
                return Optional.of(FailReason.INSUFFICIENT_STOCK);
            }
        }
        return Optional.empty();
    }

    private Optional<FailReason> validateStockLine(OrderLine line, int stock) {
        int qty = line.getQuantity();

        if (qty < 1) {
            return Optional.of(FailReason.INVALID_QUANTITY);
        }
        if (qty > stock) {
            return Optional.of(FailReason.INSUFFICIENT_STOCK);
        }
        return Optional.empty();
    }

}
