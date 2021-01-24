package connect;

import java.sql.*;

public class ConnectionService {
    private ConnectionService() {
    }

    public static Connection connect() {
        try {
            return DriverManager.getConnection("jdbc:mysql://localhost:3306/SpaceChat?serverTimezone=Europe/Moscow", "root", "jpwv8zcm17VNP");
        } catch (SQLException throwables) {
            throw new RuntimeException("SWW", throwables);
        }
    }

    public static void rollback(Connection connection) {
        if(connection == null){
            return;
        }
        try {
            connection.rollback();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public static void close(Connection connection) {
        if(connection == null){
            return;
        }
        try {
            connection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

}


