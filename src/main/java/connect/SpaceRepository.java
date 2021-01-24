package connect;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;



public class SpaceRepository {
    static String authServ(String login, String password){
        Objects.requireNonNull(login, "Login can not be null");
        Objects.requireNonNull(password, "Password can not be null");
        Connection connection = null;
        if(login.isEmpty()){
            throw new IllegalArgumentException("Login can not be neither blank");
        }
        if(password.isEmpty()){
            throw new IllegalArgumentException("Password can not be blank");
        }
        try{
            connection = ConnectionService.connect();
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM users WHERE login = ? AND password = ?");
            statement.setString(1,login);
            statement.setString(2, password);
            ResultSet rs = statement.executeQuery();
            if(rs.next()){
                return rs.getString("nickname");
            }
            return null;
        } catch (Exception e){
            throw new RuntimeException("SWW",e);
        }finally {
            ConnectionService.close(connection);
        }

    }

    public static boolean createUsers(String login, String password, String nickname) {
        Connection connection = null;
        if(login.equals(checkUserLogin(login)) || nickname.equals((checkUserNick(nickname)))){
            return false;
        } else{
        try {
            connection = ConnectionService.connect();
            connection.setAutoCommit(false);
            PreparedStatement statement = connection.prepareStatement("INSERT INTO users (login, password, nickname) VALUES (?,?,?)");
            statement.setString(1, login);
            statement.setString(2, password);
            statement.setString(3, nickname);

            statement.executeUpdate();
            connection.commit();


            return true;
        } catch (SQLException e) {
            ConnectionService.rollback(connection);
            throw new RuntimeException("SWW", e);
        } finally {
            ConnectionService.close(connection);
        }

        }
    }



    public static boolean changeNickname(String trueNickname, String newNickname) {
        Objects.requireNonNull(trueNickname, "User can not be null");
        Connection connection = null;
        try {
            connection = ConnectionService.connect();
            connection.setAutoCommit(false);
            PreparedStatement statement = connection.prepareStatement("UPDATE user SET nickname = ? WHERE nickname = ?");
            statement.setString(1, newNickname);
            statement.setString(2, trueNickname);
            statement.executeUpdate();
            connection.commit();
            return true;
        } catch (SQLException e) {
            ConnectionService.rollback(connection);
            return false;
        } finally {
            ConnectionService.close(connection);
        }
    }

    public static String checkUserLogin(String login){
        Connection connection = null;
        try{
            connection = ConnectionService.connect();
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM users WHERE login = ? ");
            statement.setString(1,login);

            ResultSet rs = statement.executeQuery();
            if(rs.next()){
                return
                rs.getString("login");
            }
            return null;
        } catch (Exception e){
            throw new RuntimeException("SWW",e);
        }finally {
            ConnectionService.close(connection);
        }
    }
    private static String checkUserNick(String nickname) {
        Connection connection = null;
        try{
            connection = ConnectionService.connect();
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM users WHERE nickname = ? ");
            statement.setString(1,nickname);

            ResultSet rs = statement.executeQuery();
            if(rs.next()){
                return
                        rs.getString("nickname");
            }
            return null;
        } catch (Exception e){
            throw new RuntimeException("SWW",e);
        }finally {
            ConnectionService.close(connection);
        }
    }
}
