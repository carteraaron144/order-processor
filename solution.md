Database: PostgreSQL
     Tables:
        Orders {
            order_id: int (pk)
            order_time: TIMESTAMP
            order_status: ENUM -PENDING, FULFILLED, REJECTED
        }
        OrderLines {
            line_id: int (pk)
            order_id: int (fk)
            item_id: int (fk)
            unit_price: NUMERIC(12, 2)
            quantity: int
        }
        FailedOrders {
            id: int (pk)
            order_id: int (fk)
            failure_reason: ENUM -INSUFFICIENT_STOCK, INVALID_QUANTITY, ITEM_NOT_FOUND, UNKNOWN_FAILURE
        }
        Items {
            item_id: int (pk)
            sku: varchar(32)
            item_name: varchar(126)
            description: varchar(512)
            price: NUMERIC(12, 2)
            quantity_in_stock: int
            updated_date: TIMESTAMP
        }
