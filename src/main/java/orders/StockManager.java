package orders;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class StockManager{
    DBHelper db;
    RowMapper<Item> itemRowMapper = row -> {
        int itemId = row.getInt("item_id");
        String item_sku = row.getString("sku");
        String itemName = row.getString("item_name");
        String description = row.getString("description");
        BigDecimal price = row.getBigDecimal("price");
        int quantity_in_stock = row.getInt("quantity_in_stock");
        LocalDateTime updated_date = row.getTimestamp("updated_date").toLocalDateTime();
        return new Item(itemId, item_sku, itemName, description, price, quantity_in_stock, updated_date);
    };

    StockManager(DBHelper dbHelper) {
        this.db = dbHelper;
    }

    int getStock(int itemId) {
        String sql = "SELECT quantity_in_stock FROM Items WHERE item_id=?;";
        RowMapper<Integer> mapper = rs -> rs.getInt("quantity_in_stock");
        return db.query(sql, mapper, itemId).getFirst();
    }

    Item getItem(String sku) {
        String sql = "SELECT * FROM Items WHERE SKU=?";

        return db.query(sql, itemRowMapper, sku).getFirst();
    }

    int decrementStock(int itemId, int qty) {
        //attempt to reduce stock in item table. Check and subtract happen in one statement,
        //so two processors can't both pass the check. Returns 0 if there wasn't enough stock.
        String sql = "UPDATE Items SET quantity_in_stock = quantity_in_stock - ? WHERE item_id = ? AND quantity_in_stock >= ?";

        return db.executeUpdate(sql, qty, itemId, qty);
    }

    List<Item> getLowStock(int threshold) {
        /*
        Returns all items with quantity_in_stock below the given threshold value
         */
        String sql = "SELECT * FROM Items WHERE quantity_in_stock < ?";
        return db.query(sql, itemRowMapper, threshold);
    }
}
