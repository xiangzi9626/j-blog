package cn.example.blog.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Jdbc {
    private static Connection conn;
    private static String driverClassName="com.mysql.cj.jdbc.Driver";
    private static String url="jdbc:mysql://localhost:3306/crm?serverTimezone=Asia/Shanghai";
    private static String username="root";
    private static String password="root";

    static{
        try {
              Class.forName(driverClassName);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static Connection getConnection() throws SQLException {
        try {
            conn = DriverManager.getConnection(url,username,password);
        }catch(SQLException e) {
            e.getLocalizedMessage();
        }
        return conn;
    }
}
