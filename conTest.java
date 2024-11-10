import java.sql.*;


public class conTest {
        public static void main(String[] args) {


            try{

                Connection connection = DriverManager.getConnection(
                        "jdbc:mysql://127.0.0.1:3306/phase4_schema",
                        "root",
                        "SmoothBeats@321");


                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery("select * from users");

                while(resultSet.next()) {
                    System.out.println(resultSet.getString("username"));
                    System.out.println(resultSet.getString("password"));

            }

        } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        public static final String DB_URL = "jdbc:mysql://127.0.0.1:3306/phase4_schema";
}
