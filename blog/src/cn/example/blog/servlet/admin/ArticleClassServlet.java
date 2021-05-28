package cn.example.blog.servlet.admin;

import cn.example.blog.bean.Dtree;
import cn.example.blog.bean.MenuVo;
import cn.example.blog.util.DB;
import cn.example.blog.util.DtreeUtil;
import cn.example.blog.util.GetJsonUtil;
import cn.example.blog.util.TreeUtil;
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
import java.util.*;

@WebServlet("/admin/article_class")
public class ArticleClassServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Map reqMap = GetJsonUtil.toMap(req, resp);
        PrintWriter out = resp.getWriter();
        String action = reqMap.get("action").toString().trim();
        String id = "";
        String pid = "";
        String title = "";
        switch (action) {
            case "add":
                System.out.print("abcd");
                pid = reqMap.get("pid").toString().trim();
                title = reqMap.get("title").toString().trim();
                try {
                    int add = DB.insert("insert into class(pid,title) values(?,?)", pid, title);
                    out.print("ok");
                } catch (SQLException throwables) {
                    out.print("提交失败,请重试");
                    throwables.printStackTrace();
                }
                break;
            case "edit":
                id = reqMap.get("id").toString().trim();
                pid = reqMap.get("pid").toString().trim();
                title = reqMap.get("title").toString().trim();
                try {
                    ArrayList<JSONObject> row = DB.select("select * from class where id=?", id);
                    int count = DB.count("select id from class where pid=?", id);
                    if (count > 0 && !row.get(0).get("pid").equals(pid)) {
                        out.print("该分类下面有子分类不能修改上级分类");
                        return;
                    }
                    if (id.equals(pid)) {
                        out.print("上级分类选择有误,请重新选择");
                        return;
                    }
                    int up = DB.update("update class set pid=?,title=? where id=?", pid, title, id);
                    out.print("ok");
                } catch (SQLException throwables) {
                    out.print("提交失败,请重试");
                    throwables.printStackTrace();
                }
                break;
            case "delete":
                id = reqMap.get("id").toString().trim();
                try {
                    int count = DB.count("select id from class where pid=?", id);
                    if (count > 0) {
                        out.print("请先删除下级子分类");
                        return;
                    }
                    int del = DB.delete("delete from class where id=?", id);
                    out.print("ok");
                } catch (SQLException throwables) {
                    out.print("删除失败,请重试");
                    throwables.printStackTrace();
                }
                break;
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html;charset=utf-8");
        PrintWriter out = resp.getWriter();
        String action = req.getParameter("action").trim();
        switch (action) {
            case "list":
                try {
                    class_list(out);
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
                break;
            case "dtree":
                try {
                    Map<String, Object> dtree = this.dtree();
                    List<Dtree> dtreeList = (List<Dtree>) dtree.get("data");
                    Dtree d = new Dtree();
                    d.setId(Long.parseLong("0"));
                    d.setParentId(Long.parseLong("0"));
                    d.setTitle("顶级分类");
                    d.setLast(true);
                    List<Dtree> dl = new ArrayList<>();
                    dl.add(d);
                    for (int i = 0; i < dtreeList.size(); i++) {
                        dl.add(dtreeList.get(i));
                    }
                    dtree.put("data", dl);
                    Gson gson = new Gson();
                    String dtreeJson = gson.toJson(dtree);
                    out.print(dtreeJson);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case "getClass":
                String id = req.getParameter("id").toString().trim();
                List<JSONObject> getClass = null;
                try {
                    getClass = DB.select("select * from class where id=?", id);
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
                JSONObject jc = getClass.get(0);
                String pid = jc.get("pid") + "";
                if (pid.equals("0")) {
                    jc.put("p_name", "顶级分类");
                } else {
                    try {
                        List<JSONObject> pc = DB.select("select title from class where id=?", pid);
                        jc.put("p_name", pc.get(0).get("title"));
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                }
                out.print(jc);
                break;
        }
    }

    public Map<String, Object> menu() throws Exception {
        Map<String, Object> map = new LinkedHashMap<>();
        Map<String, Object> home = new HashMap<>();
        Map<String, Object> logo = new HashMap<>();
        List<JSONObject> menuList = DB.select("select * from system_menu");
        List<MenuVo> menuInfo = new ArrayList<>();
        for (Object e : menuList) {
            MenuVo menuVO = new MenuVo();
            JSONObject jb = (JSONObject) e;
            menuVO.setId(Long.parseLong(jb.get("id") + ""));
            menuVO.setPid(Long.parseLong(jb.get("pid") + ""));
            menuVO.setHref(jb.get("href") + "");
            menuVO.setTitle(jb.get("title") + "");
            menuVO.setIcon(jb.get("icon") + "");
            menuVO.setTarget(jb.get("target") + "");
            menuInfo.add(menuVO);
        }
        home.put("title", "首页");
        home.put("href", "/layuimini-2/page/welcome.html");//控制器路由,自行定义
        logo.put("title", "后台管理系统");
        logo.put("image", "/layuimini-2/images/logo.png");//静态资源文件路径,可使用默认的logo.png
        map.put("homeInfo", home);
        map.put("logoInfo", logo);
        map.put("menuInfo", TreeUtil.toTree(menuInfo, 0L));
        return map;
    }

    public void class_list(PrintWriter out) throws SQLException {
        List<JSONObject> data = DB.select("select * from class");
        JSONObject jb = new JSONObject();
        jb.put("code", 0);
        jb.put("msg", "");
        jb.put("count", data.size());
        jb.put("data", data);
        out.print(jb);
    }

    public Map<String, Object> dtree() throws Exception {
        Map<String, Object> map = new LinkedHashMap<>();
        List<JSONObject> menuList = DB.select("select * from class");
        List<Dtree> menuInfo = new ArrayList<>();
        for (Object e : menuList) {
            Dtree dtree = new Dtree();
            JSONObject jb = (JSONObject) e;
            if (jb.get("title").equals("菜单管理")) {
                continue;
            }
            dtree.setId(Long.parseLong(jb.get("id") + ""));
            dtree.setParentId(Long.parseLong(jb.get("pid") + ""));
            dtree.setTitle(jb.get("title") + "");
            boolean b = true;
            for (int i = 0; i < menuList.size(); i++) {
                JSONObject mjb = menuList.get(i);
                if (jb.get("id").equals(mjb.get("pid")) || jb.get("pid").equals(0)) {
                    b = false;
                    dtree.setLast(b);
                    break;
                }
                dtree.setLast(b);
            }
            menuInfo.add(dtree);
        }
        JSONObject status = new JSONObject();
        status.put("code", 200);
        status.put("message", "操作成功");
        map.put("status", status);
        map.put("data", DtreeUtil.toTree(menuInfo, 0L));
        return map;
    }

    public void edit(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Map map = GetJsonUtil.toMap(req, resp);
        PrintWriter out = resp.getWriter();
        String action = map.get("action").toString().trim();
        System.out.print(map);
    }
}