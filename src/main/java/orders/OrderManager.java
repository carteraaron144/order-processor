package orders;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

public class OrderManager{
    DBHelper db;
    RowMapper<Order> orderRowMapper = row -> {
        int order_id = row.getInt("order_id");
        LocalDateTime order_time = row.getTimestamp("order_time").toLocalDateTime();
        OrderStatus order_status = OrderStatus.valueOf(row.getString("order_status"));
        return new Order(order_id, order_time, order_status, new ArrayList<>());
    };

    OrderManager(DBHelper dbHelper){
        this.db = dbHelper;
    }

    Order getOrderById(int orderId) {
        String sql = "SELECT * FROM Orders WHERE order_id=?;";

        Order order = db.query(sql, orderRowMapper, orderId).getFirst();
        order.setItems(getOrderLines(order.getID()));
        return order;
    }

    Optional<Order> getNextOrder() {
        String sql = "SELECT * FROM Orders WHERE order_status='PENDING' ORDER BY order_time, order_id;";
        Order order;

        try {
            order = db.query(sql, orderRowMapper).getFirst();
        } catch (NoSuchElementException e) {
            return Optional.empty();
        }

        order.setItems(getOrderLines(order.getID()));
        return Optional.of(order);
    }

    List<OrderLine> getOrderLines(int orderId) {
        String sql = "SELECT * FROM order_lines WHERE order_id=?";
        RowMapper<OrderLine> mapper = row -> {
            int line_id = row.getInt("line_id");
            int order_id = row.getInt("order_id");
            int item_id = row.getInt("item_id");
            int quantity = row.getInt("quantity");
            BigDecimal unit_price = row.getBigDecimal("unit_price");
            return new OrderLine(line_id, order_id, item_id, quantity, unit_price);
        };
        return db.query(sql, mapper, orderId);
    }

    int setOrderStatus(Order order, OrderStatus status) {
        String sql = "UPDATE Orders SET order_status=?::order_status WHERE order_id = ?";
        return db.executeUpdate(sql, status.toString(), order.getID());
    }

    /*
    Counts one crashed processing attempt and saves the error. After maxAttempts the order
    becomes FAILED, so getNextOrder() stops picking it up. Returns the order's resulting status.
    In SET, every expression sees the row's old values, so both "attempts + 1" are the same number.
     */
    OrderStatus recordFailedAttempt(Order order, int maxAttempts, String error) {
        String sql = """
            UPDATE orders
            SET attempts     = attempts + 1,
                last_error   = ?,
                order_status = CASE WHEN attempts + 1 >= ? THEN 'FAILED'::order_status
                                    ELSE order_status END
            WHERE order_id = ?
            RETURNING order_status
            """;
        return db.query(sql, row -> OrderStatus.valueOf(row.getString("order_status")),
                error, maxAttempts, order.getID()).getFirst();
    }

    int postNewOrder() {
        String sql = "";
        return 0;
    }
}
