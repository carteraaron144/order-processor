package orders;

import java.sql.ResultSet;
import java.sql.SQLException;

@FunctionalInterface
public interface RowMapper<T> {
    T rowToObject(ResultSet resultSet) throws SQLException;
}