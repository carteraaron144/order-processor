package orders;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class Order {

    private final int id;

    public void setItems(List<OrderLine> items) {
        this.items = items;
    }

    private List<OrderLine> items;

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    private OrderStatus status;
    private LocalDateTime orderTime;

    Order(int id, LocalDateTime time, OrderStatus status, List<OrderLine> items) {
        this.id = id;
        this.orderTime = time;
        this.status = status;
        this.items = items;
    }

    BigDecimal getTotalPrice() {
        //Add price of each line together.
        return items.stream()
                .map(OrderLine::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    int getID() {
        return id;
    }

    List<OrderLine> getItems() {
        return items;
    }

    OrderStatus getStatus() {
        return status;
    }

    LocalDateTime getTime() {
        return orderTime;
    }

    @Override
    public String toString() {
        String out = "";
        out = out.concat("id: ").concat(Integer.toString(id)).concat("\n");
        out = out.concat("order_time: ").concat(orderTime.toString()).concat("\n");
        out = out.concat("status: ").concat(status.toString()).concat("\n");
        out = out.concat("items: {\n");
        for (OrderLine line : items) {
            out = out.concat(line.toString()).concat("\n");
        }
        out = out.concat("}");
        return out;
    }
}
