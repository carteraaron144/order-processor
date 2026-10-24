package orders;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Item {
    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public int getQuantity_in_stock() {
        return quantity_in_stock;
    }

    public void setQuantity_in_stock(int quantity_in_stock) {
        this.quantity_in_stock = quantity_in_stock;
    }

    public LocalDateTime getUpdated_date() {
        return updated_date;
    }

    public void setUpdated_date(LocalDateTime updated_date) {
        this.updated_date = updated_date;
    }

    private int itemId;
    private String sku;
    private String itemName;
    private String description;
    private BigDecimal price;
    private int quantity_in_stock;
    private LocalDateTime updated_date;

    Item(int itemId, String sku, String itemName, String description, BigDecimal price, int quantity_in_stock, LocalDateTime updated_date) {
        this.itemId = itemId;
        this.sku = sku;
        this.itemName = itemName;
        this.description = description;
        this.price = price;
        this.quantity_in_stock = quantity_in_stock;
        this.updated_date = updated_date;
    }
}
