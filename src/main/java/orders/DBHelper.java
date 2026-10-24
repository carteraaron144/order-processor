package orders;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.*;

public class DBHelper {
    private final String url = "jdbc:postgresql://localhost:5432/order_processor";
    private final Properties props;
    private Connection conn;
    private Logger logger = Logger.getGlobal();

    DBHelper() {
        this.props = new Properties();
        props.setProperty("user", "aaroncarter");
        props.setProperty("password", "");
        //props.setProperty("ssl", "true");

        try {
            this.conn = DriverManager.getConnection(url, props);
        } catch (SQLException e) {
            logger.severe("Failed to connect to database.");
            throw new RuntimeException(e);
        }
    }

    public <T> List<T> query(String sql, RowMapper<T> mapper, Object... params) {
        List<T> results = new ArrayList<>();
        try (PreparedStatement st = conn.prepareStatement(sql)) {
            loadParameters(st, params);
            try (ResultSet rs = st.executeQuery()){
                while (rs.next()) {
                    results.add(mapper.rowToObject(rs));
                }
                return results;
            }
        } catch (SQLException e) {
            logger.severe(String.format("Query Failed: %s", sql));
            logger.severe(e.toString());
            throw new RuntimeException(e);
        }
    }

    public int executeUpdate(String sql, Object... params) {
        int rows = 0;
        try (PreparedStatement st = conn.prepareStatement(sql)) {
            loadParameters(st, params);
            rows = st.executeUpdate();
            return rows;
        } catch (SQLException e) {
            logger.severe(String.format("Update Failed: %s", sql));
            logger.severe(e.toString());
            throw new RuntimeException(e);
        }
    }

    public void setAutoCommit(boolean autoCommitOn) throws SQLException {
        conn.setAutoCommit(autoCommitOn);
    }

    public void commit() throws SQLException {
        conn.commit();
    }

    public void rollback() throws SQLException {
        conn.rollback();
    }

    void loadParameters(PreparedStatement st, Object... params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            st.setObject(i+1, params[i]);
        }
    }
}
