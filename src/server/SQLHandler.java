package server;

import java.sql.*;

public class SQLHandler {
    private static Connection connection;
    private static PreparedStatement statement;



    public static void connect(String dbName) throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC");
        connection = DriverManager.getConnection("jdbc:sqlite:" + dbName);
    }

    public static String getNick(String login, String password) throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(
                "select nick from \"users_table\" where login = \"" + login
                + "\" and password = \"" + password + "\"");
        String nick = null;
        while (rs.next()) {
            nick = rs.getString("nick");
        }
        return nick;
    }
}
