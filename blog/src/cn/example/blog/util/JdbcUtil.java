package cn.example.blog.util;
import com.alibaba.druid.pool.DruidDataSourceFactory;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class JdbcUtil {
    private static DataSource ds;
    private static Properties p;
     /*private static Connection conn;
    private static String driverClassName="com.mysql.cj.jdbc.Driver";
    private static String url="jdbc:mysql://localhost:3306/crm?serverTimezone=Asia/Shanghai";
    private static String username="root";
    private static String password="root";*/

    static{
        try {
            p=new Properties();
            p.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("druid.properties"));
            //使用连接池连接数据库
           ds= DruidDataSourceFactory.createDataSource(p);
            // Class.forName(p.getProperty("driverClassName"));
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
private JdbcUtil(){}
    public static Connection getConnection() throws SQLException {
       /* try {
            conn = DriverManager.getConnection(p.getProperty("url"),p.getProperty("user"),p.getProperty("password"));
        } catch (Exception e) {
            e.getLocalizedMessage();
        }*/
        return ds.getConnection();
    }
}
