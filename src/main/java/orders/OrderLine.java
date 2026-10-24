package orders;

import java.math.BigDecimal;

public class OrderLine {
    private final int id;
    private final int orderId;
    private final int itemId;
    private final int quantity;
    private BigDecimal unitPrice;

    OrderLine (int id, int orderId, int itemId, int quantity, BigDecimal unitPrice){
        this.id = id;
        this.orderId = orderId;
        this.itemId = itemId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public int getId() {
        return id;
    }
    public int getOrderId() {
        return orderId;
    }
    public int getItemId() {
        return itemId;
    }
    public int getQuantity() {
        return quantity;
    }
    public BigDecimal getUnitPrice() {
        return unitPrice;
    }
    public BigDecimal getTotalPrice() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }

    @Override
    public String toString() {
        String out = "";
        out = out.concat("id: ").concat(Integer.toString(id).concat("\n"));
        out = out.concat("order_id: ").concat(Integer.toString(orderId).concat("\n"));
        out = out.concat("item_id: ").concat(Integer.toString(itemId).concat("\n"));
        out = out.concat("quantity: ").concat(Integer.toString(quantity).concat("\n"));
        out = out.concat("unit_price: ").concat(String.valueOf(unitPrice).concat("\n"));
        return out;
    }
}
