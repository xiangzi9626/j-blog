package cn.example.blog.servlet.admin;

import cn.example.blog.util.DB;
import cn.example.blog.util.GetJsonUtil;
import cn.example.blog.util.MD5Util;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

@WebServlet("/admin_login")
public class AdminLoginServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Map map = GetJsonUtil.toMap(req, resp);
        PrintWriter out = resp.getWriter();
        String action = map.get("action").toString().trim();
        if (action.equals("logout")) {
            req.getSession().invalidate();
            if (req.getSession().getAttribute("user") == null) {
                out.print("ok");
            } else {
                out.print("error");
            }
            return;
        }
        String username = map.get("username").toString().trim();
        String password = map.get("password").toString();
        password = MD5Util.getMD5Str(password);
        String captcha = map.get("captcha").toString().trim();
        String sessionCaptcha = req.getSession().getAttribute("captcha").toString();
        if (captcha == null || "".equals(captcha)) {
            out.print("验证码不能为空");
            return;
        } else if (!captcha.equalsIgnoreCase(sessionCaptcha)) {
            out.print("验证码错误");
            return;
        }
        ArrayList userList = null;
        try {
            userList = DB.select("select * from user where username=? and password=? and level<?", username, password, 3);
        } catch (SQLException throwables) {
            out.print("提交失败,请重试");
            throwables.printStackTrace();
            userList.clear();
            return;
        }
        if (userList.size() == 0) {
            userList.clear();
            out.print("账号或密码错误");
        } else {
            req.getSession().setAttribute("user", userList.get(0));
            out.print("ok");
        }
        out.close();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Object currentUser = req.getSession().getAttribute("user");
        if (currentUser != null) {
            resp.sendRedirect("/admin/index");
            return;
        }
        req.getRequestDispatcher("/WEB-INF/admin/login.html").forward(req, resp);
    }
}
