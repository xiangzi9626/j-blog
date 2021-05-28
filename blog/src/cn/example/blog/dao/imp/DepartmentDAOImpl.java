package cn.example.blog.dao.imp;

import cn.example.blog.dao.IDepartmentDAO;
import cn.example.blog.bean.Department;
import cn.example.blog.util.JdbcUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DepartmentDAOImpl implements IDepartmentDAO {
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    @Override
    public String insert(Department d, String success, String error) {
        try {
            conn = JdbcUtil.getConnection();
            ps = conn.prepareStatement("insert into department(name,sn) values(?,?)");
            ps.setString(1, d.getName());
            ps.setString(2, d.getSn());
            ps.execute();
            return success;
        } catch (Exception e) {
            return error;
            //e.printStackTrace();
        } finally {
            this.close(conn, ps, rs);
        }
    }

    @Override
    public String update(Department d, String success, String error) {
        try {
            conn = JdbcUtil.getConnection();
            ps = conn.prepareStatement("update department set name=?,sn=? where id=?");
            ps.setString(1, d.getName());
            ps.setString(2, d.getSn());
            ps.setLong(3, d.getId());
            ps.executeUpdate();
            return success;
        } catch (SQLException e) {
            return error;
            //e.printStackTrace();
        } finally {
            this.close(conn, ps, rs);
        }
    }

    @Override
    public String delete(Long id, String success, String error) {
        try {
            conn = JdbcUtil.getConnection();
            ps = conn.prepareStatement("delete from department where id=?");
            ps.setLong(1, id);
            ps.execute();
            return success;
        } catch (SQLException e) {
            return error;
            //e.printStackTrace();
        } finally {
            this.close(conn, ps, rs);
        }
    }

    @Override
    public Department selectOne(Long id) {
        try {
            conn = JdbcUtil.getConnection();
            ps = conn.prepareStatement("select * from department where id=?");
            ps.setLong(1, id);
            rs = ps.executeQuery();
            if (rs.next()) {
                Department d = new Department();
                d.setId(id);
                d.setName(rs.getString("name"));
                d.setSn(rs.getString("sn"));
                return d;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            this.close(conn, ps, rs);
        }
        return null;
    }

    @Override
    public List<Department> selectAll() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Department> list = new ArrayList<>();
        try {
            conn = JdbcUtil.getConnection();
            ps = conn.prepareStatement("select * from department");
            rs = ps.executeQuery();
            while (rs.next()) {
                Department d = new Department();
                d.setId(rs.getLong("id"));
                d.setName(rs.getString("name"));
                d.setSn(rs.getString("sn"));
                list.add(d);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            } finally {
                try {
                    if (ps != null) {
                        ps.close();
                    }
                } catch (Exception e3) {
                    e3.printStackTrace();
                } finally {
                    try {
                        if (conn != null) {
                            conn.close();
                        }
                    } catch (Exception e4) {
                        e4.printStackTrace();
                    }
                }
            }
        }
        return list;
    }

    public void close(Connection conn, PreparedStatement ps, ResultSet rs) {
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
