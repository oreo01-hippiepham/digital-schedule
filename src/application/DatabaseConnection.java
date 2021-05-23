package application;
import java.sql.Connection;
import java.sql.*;

public class DatabaseConnection {

    public Connection connection;
    public Connection getConnection()
    {
        String databaseName = "mock_calendar";
        String databaseUser = "root";
        String databasePassword = "Xnovastrike1";
        String url = "jdbc:mysql://172.16.16.202:3306/" + databaseName;
        try
        {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(url, databaseUser, databasePassword);
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        return connection;
    }
}
