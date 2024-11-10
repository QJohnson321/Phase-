import com.mysql.cj.jdbc.DatabaseMetaData;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class sqliteTest {
        public static void main(String[] args) {
            // Path to your SQLite database file
            // Path to your SQLite database file
            String dbUrl = "jdbc:sqlite:C:/Sqlite3/mydb.db";  // Adjust this path to your database location

            // Try to establish a connection
            try (Connection connection = DriverManager.getConnection(dbUrl)) {
                if (connection != null) {
                    System.out.println("Connection Successful!");
                }
            } catch (SQLException e) {
                System.out.println("Connection failed: " + e.getMessage());
            }
        }
    }
