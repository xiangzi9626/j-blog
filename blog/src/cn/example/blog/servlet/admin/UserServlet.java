package cn.example.blog.servlet.admin;

import cn.example.blog.util.DB;
import cn.example.blog.util.GetJsonUtil;
import cn.example.blog.util.MD5Util;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@WebServlet("/admin/user")
public class UserServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Map reqMap = GetJsonUtil.toMap(req, resp);
        PrintWriter out = resp.getWriter();
        String action = reqMap.get("action").toString().trim();
        String id = "";
        String username = "";
        String password = "";
        String password2 = "";
        String level = "";
        String phone = "";
        JSONObject user;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date time = new Date();
        switch (action) {
            case "modify_admin_password":
                user = (JSONObject) req.getSession().getAttribute("user");
                id = user.get("id").toString().trim();
                String old_password = MD5Util.getMD5Str(reqMap.get("old_password").toString());
                String new_password = reqMap.get("new_password").toString();
                String again_password = reqMap.get("again_password").toString();
                if (new_password.length() < 6) {
                    out.print("请输入6-30位新密码");
                    return;
                }
                if (!new_password.equals(again_password)) {
                    out.print("新密码两次输入不一致");
                    return;
                }
                try {
                    int count = DB.count("select * from user where id=? and password=?", id, old_password);
                    if (count == 0) {
                        out.print("旧密码不正确");
                        return;
                    }
                    DB.update("update user set password=? where id=?", MD5Util.getMD5Str(new_password), id);
                    out.print("ok");
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
                break;
            case "get_user":
                try {
                    id = reqMap.get("id").toString().trim();
                    List<JSONObject> res = DB.select("select * from user where id=?", id);
                    out.print(res.get(0));
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
                break;
            case "delete":
                id = reqMap.get("id").toString().trim();
                user = (JSONObject) req.getSession().getAttribute("user");
                if (!user.get("level").equals("1")) {
                    out.print("不是超级管理员权限不足");
                    return;
                }
                try {
                    DB.delete("delete from user where id=?", id);
                    out.print("ok");
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
                break;
            case "add":
                username = reqMap.get("username").toString().trim();
                password = MD5Util.getMD5Str(reqMap.get("password").toString());
                password2 = MD5Util.getMD5Str(reqMap.get("password2").toString());
                phone = reqMap.get("phone").toString().trim();
                level = reqMap.get("level").toString().trim();
                user = (JSONObject) req.getSession().getAttribute("user");
                if (!user.get("level").equals("1")) {
                    out.print("不是超级管理员权限不够");
                    return;
                }
                if (reqMap.get("password").toString().length() < 6) {
                    out.print("请输入6-30位密码");
                    return;
                } else if (!password.equals(password2)) {
                    out.print("两次密码输入不一致");
                    return;
                }
                if (!Pattern.matches("^[1-9][0-9]{10}$", phone)) {
                    out.print("请输入11位手机号");
                    return;
                }
                try {
                    int user_count = DB.count("select * from user where username=?", username);
                    int phone_count = DB.count("select * from user where phone=?", phone);
                    if (user_count > 0) {
                        out.print("账号已存在不可用");
                        return;
                    }
                    if (phone_count > 0) {
                        out.print("手机号已存在不可用");
                        return;
                    }
                    DB.insert("insert into user(username,password,phone,level,create_time) values(?,?,?,?,?)", username, password, phone, level, time);
                    out.print("ok");
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }

                break;
            case "edit":
                id = reqMap.get("id").toString().trim();
                username = reqMap.get("username").toString().trim();
                password = MD5Util.getMD5Str(reqMap.get("password").toString());
                password2 = MD5Util.getMD5Str(reqMap.get("password2").toString());
                phone = reqMap.get("phone").toString().trim();
                // level = reqMap.get("level").toString().trim();
                user = (JSONObject) req.getSession().getAttribute("user");
                if (!user.get("level").equals("1")) {
                    out.print("不是超级管理员权限不够");
                    return;
                }
                if (!reqMap.get("password").equals("") && reqMap.get("password") != null && !password.equals(password2)) {
                    out.print("两次密码输入不一致");
                    return;
                } else if (!reqMap.get("password").equals("") && reqMap.get("password") != null
                        && reqMap.get("password").toString().length() < 6) {
                    out.print("请输入6-30位密码");
                    return;
                }
                if (!Pattern.matches("^[1-9][0-9]{10}$", phone)) {
                    out.print("请输入11位手机号");
                    return;
                }
                try {
                    int user_count = DB.count("select * from user where username=? and id!=?", username, id);
                    int phone_count = DB.count("select * from user where phone=? and id!=?", phone, id);
                    if (user_count > 0) {
                        out.print("账号已存在不可用");
                        return;
                    }
                    if (phone_count > 0) {
                        out.print("手机号已存在不可用");
                        return;
                    }
                    if (reqMap.get("password").toString().length() >= 6) {
                        DB.update("update user set username=?,password=?,phone=?,create_time=? where id=?", username, password, phone, time, id);
                    } else {
                        DB.update("update user set username=?,phone=?,create_time=? where id=?", username, phone, time, id);
                    }
                    out.print("ok");
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }

                break;
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html;charset=utf-8");
        PrintWriter out = resp.getWriter();
        Map<String, String[]> reqMap = req.getParameterMap();
        String action = reqMap.get("action")[0];
        switch (action) {
            case "user_list":
                try {
                    List<JSONObject> userList = DB.select("select * from user where level>=?", 3);
                    Map map = new HashMap();
                    map.put("code", 0);
                    map.put("msg", "");
                    map.put("count", userList.size());
                    map.put("data", userList);
                    Gson gson = new Gson();
                    out.print(gson.toJson(map));
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
                break;
            case "admin_list":
                try {
                    List<JSONObject> adminList = DB.select("select * from user where level<?", 3);
                    Map map = new HashMap();
                    map.put("code", 0);
                    map.put("msg", "");
                    map.put("count", adminList.size());
                    map.put("data", adminList);
                    Gson gson = new Gson();
                    out.print(gson.toJson(map));
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
                break;
        }
    }
}
