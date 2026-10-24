package orders;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.*;

public class Main {
    DBHelper dbHelper = new DBHelper();
    StockManager stockManager = new StockManager(dbHelper);
    OrderManager orderManager = new OrderManager(dbHelper);
    OrderProcessor orderProcessor = new OrderProcessor(stockManager, orderManager, dbHelper);
    Reports reports = new Reports(stockManager, orderManager);
    LogManager logManager = LogManager.getLogManager();
    Logger logger = Logger.getGlobal();


    public void main() {
        run();
    }

    public void run() {
        List<Order> newOrders = new ArrayList<>();
        Optional<Order> order;
        try {
            order = orderProcessor.processNextOrder();
            while (order.isPresent()) {
                // A crashed order that will be retried comes back still PENDING; only report final results.
                if (order.get().getStatus() != OrderStatus.PENDING) {
                    newOrders.add(order.get());
                }
                order = orderProcessor.processNextOrder();
            }
        }
        catch (RuntimeException e) {
            // Only reached when the database itself is failing; remaining orders stay PENDING for the next run.
            logger.log(Level.SEVERE, "Processing stopped; remaining orders left PENDING", e);
        }
        finally {
            reports.lowStock();
            reports.ordersProcessed(newOrders);
            reports.totalRevenue(newOrders);
        }


        //Run reports

    }
}
