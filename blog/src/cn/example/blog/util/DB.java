package cn.example.blog.util;

import com.alibaba.fastjson.JSONObject;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DB {
    private static Connection conn;
    private static ResultSet rs = null;
    private static PreparedStatement ps = null;

    public static int insert(String sql, Object... parameter) throws SQLException {
        conn = JdbcUtil.getConnection();
        ps = conn.prepareStatement(sql);
        for (int i = 0; i < parameter.length; i++) {
            ps.setObject(i + 1, parameter[i]);
        }
        int i = ps.executeUpdate();
        close();
        return i;
    }

    public static int delete(String sql, Object... parameter) throws SQLException {
        conn = JdbcUtil.getConnection();
        ps = conn.prepareStatement(sql);
        for (int i = 0; i < parameter.length; i++) {
            ps.setObject(i + 1, parameter[i]);
        }
        int i = ps.executeUpdate();
        close();
        return i;
    }

    public static int update(String sql, Object... parameter) throws SQLException {
        conn = JdbcUtil.getConnection();
        ps = conn.prepareStatement(sql);
        for (int i = 0; i < parameter.length; i++) {
            ps.setObject(i + 1, parameter[i]);
        }
        int i = ps.executeUpdate();
        close();
        return i;
    }

    public static ArrayList<JSONObject> select(String sql, Object... parameter) throws SQLException {
        conn = JdbcUtil.getConnection();
        ps = conn.prepareStatement(sql);
        for (int i = 0; i < parameter.length; i++) {
            ps.setObject(i + 1, parameter[i]);
        }
        rs = ps.executeQuery();
        ResultSetMetaData data = rs.getMetaData();
        ArrayList<JSONObject> al = new ArrayList<>();
        while (rs.next()) {
            JSONObject jbAdd = new JSONObject();
            for (int i = 1; i <= data.getColumnCount(); i++) {// 数据库里从 1 开始
                String c = data.getColumnName(i);
                String v = rs.getString(c);
                jbAdd.put(c, v);
            }
            al.add(jbAdd);
        }
        close();
        return al;
    }

    public static <T> List<T> selectObj(String sql, Class<T> type, Object... parameter) throws Exception {
        conn = JdbcUtil.getConnection();
        List<T> list = new ArrayList<>();
        ps = conn.prepareStatement(sql);
        for (int i = 0; i < parameter.length; i++) {
            ps.setObject(i + 1, parameter[i]);
        }
        rs = ps.executeQuery();
        while (rs.next()) {
            //使用反射来创建对象
            T obj = type.newInstance();
            BeanInfo beaninfo = Introspector.getBeanInfo(obj.getClass(), Object.class);
            //获取到对象的属性描述器
            PropertyDescriptor[] pds = beaninfo.getPropertyDescriptors();
            for (PropertyDescriptor pd : pds) {
                //属性名
                String propertyName = pd.getName();
                //根据属性名获取对应列中的值
                Object value = rs.getObject(propertyName);
                //将获取到的值设置到javaBean对象中
                pd.getWriteMethod().invoke(obj, value);
            }
            list.add(obj);
        }
        close();
        return list;
    }

    public static int count(String sql, Object... parameter) throws SQLException {
        conn = JdbcUtil.getConnection();
        ps = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        for (int i = 0; i < parameter.length; i++) {
            ps.setObject(i + 1, parameter[i]);
        }
        rs = ps.executeQuery();
        rs.last();
        int rowCount = rs.getRow();
        int[] arr = {rowCount};
        close();
        return arr[0];
    }

    public static void close() {
        try {
            if (rs != null) {
                rs.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            } finally {
                try {
                    if (conn != null) {
                        conn.close();
                    }
                } catch (Exception e3) {
                    e3.printStackTrace();
                }
            }
        }
    }
}
