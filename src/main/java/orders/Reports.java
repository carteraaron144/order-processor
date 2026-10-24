package orders;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

public class Reports {
    OrderManager orderManager;
    StockManager stockManager;

    Reports(StockManager stockManager, OrderManager orderManager) {
        this.stockManager = stockManager;
        this.orderManager = orderManager;
    }

    public void lowStock() {
        List<Item> itemsList = stockManager.getLowStock(5);
        itemsList.sort(Comparator.comparingInt(Item::getQuantity_in_stock));
        System.out.println("***************************************************\n");
        System.out.println("**** Low Stock Report:                         ****\n");
        System.out.println("***************************************************\n");
        System.out.println("\n - SKU        - Stock Remaining");
        for (Item item : itemsList) {
            String str = String.format(" - %10S - %d", item.getSku(), item.getQuantity_in_stock());
            System.out.println(str);
        }
    }

    public void ordersProcessed(List<Order> orders) {
        for (Order order : orders ) {
            System.out.println(order.toString());
        }
    }

    public void totalRevenue(List<Order> orders) {
        BigDecimal sum = BigDecimal.ZERO;
        for (Order order : orders) {
            if (order.getStatus() == OrderStatus.FULFILLED){
                sum = sum.add(order.getTotalPrice());
            }
        }
        System.out.println("Total Revenue:");
        System.out.println(sum.toString());
    }
}
