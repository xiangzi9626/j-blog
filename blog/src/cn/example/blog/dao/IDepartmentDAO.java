package cn.example.blog.dao;

import cn.example.blog.bean.Department;

import java.util.List;

public interface IDepartmentDAO {
    /**
     * 插入部门信息
     * @param d 调用者将信息封装到DDepartment对象中然后传进来
     */
    String insert(Department d,String success,String error);
    String update(Department d,String success,String error);
    String delete(Long id,String success,String error);

    /**
     * 根据主键id查询指定的部门
     * @param id
     * @return
     */
    Department selectOne(Long id);
    List<Department> selectAll();
}
