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

@WebServlet("/admin/menu")
public class MenuServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Map reqMap = GetJsonUtil.toMap(req, resp);
        PrintWriter out = resp.getWriter();
        String action = reqMap.get("action").toString().trim();
        String id = "";
        String pid = "";
        String title = "";
        String icon = "";
        String href = "";
        String target = "";
        String sort = "";
        String status = "";
        switch (action) {
            case "status":
                id = reqMap.get("id").toString().trim();
                status = reqMap.get("status").toString().trim();
                System.out.print(status);
                if (status.equals("0.0")) {
                    status = "0";
                } else if (status.equals("1.0")) {
                    status = "1";
                }
                try {
                    int up = DB.update("update system_menu set status=? where id=?", status, id);
                    out.print("ok");
                } catch (SQLException throwables) {
                    out.print("操作失败,请重试");
                    throwables.printStackTrace();
                }
                break;
            case "add":
                pid = reqMap.get("pid").toString().trim();
                title = reqMap.get("title").toString().trim();
                icon = reqMap.get("icon").toString().trim();
                href = reqMap.get("href").toString().trim();
                target = reqMap.get("target").toString().trim();
                sort = reqMap.get("sort").toString().trim();
                try {
                    int add = DB.insert("insert into system_menu(pid,title,icon,href,target,sort) values(?,?,?,?,?,?)", pid, title, icon, href, target, sort);
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
                icon = reqMap.get("icon").toString().trim();
                href = reqMap.get("href").toString().trim();
                target = reqMap.get("target").toString().trim();
                sort = reqMap.get("sort").toString().trim();
                try {
                    ArrayList<JSONObject> row = DB.select("select * from system_menu where id=?", id);
                    int count = DB.count("select id from system_menu where pid=?", id);
                    if (count > 0 && !row.get(0).get("pid").equals(pid)) {
                        out.print("该菜单下面有子菜单不能修改上级菜单");
                        return;
                    }
                    if (id.equals(pid)) {
                        out.print("上级菜单选择有误,请重新选择");
                        return;
                    }
                    int up = DB.update("update system_menu set pid=?,title=?,icon=?,href=?,target=?,sort=? where id=?", pid, title, icon, href, target, sort, id);
                    out.print("ok");
                } catch (SQLException throwables) {
                    out.print("提交失败,请重试");
                     throwables.printStackTrace();
                }
                break;
            case "delete":
                id = reqMap.get("id").toString().trim();
                try {
                    int count = DB.count("select id from system_menu where pid=?", id);
                    if (count > 0) {
                        out.print("请先删除下级子菜单");
                        return;
                    }
                    int del = DB.delete("delete from system_menu where id=?", id);
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
            case "init":
                try {
                    Map<String, Object> menu = this.menu();
                    Gson gson = new Gson();
                    String meunJson = gson.toJson(menu);
                    out.print(meunJson);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case "list":
                try {
                    menu_list(out);
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
                    d.setTitle("顶级菜单");
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
            case "getMenu":
                String id = req.getParameter("id");
                List<JSONObject> getMenu = null;
                try {
                    getMenu = DB.select("select * from system_menu where id=?", id);
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
                JSONObject jmenu = getMenu.get(0);
                String pid = jmenu.get("pid") + "";
                if (pid.equals("0")) {
                    jmenu.put("p_name", "顶级菜单");
                } else {
                    try {
                        List<JSONObject> Pmenu = DB.select("select title from system_menu where id=?", pid);
                        jmenu.put("p_name", Pmenu.get(0).get("title"));
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                }
                out.print(jmenu);
                break;
        }
    }

    public Map<String, Object> menu() throws Exception {
        Map<String, Object> map = new LinkedHashMap<>();
        Map<String, Object> home = new HashMap<>();
        Map<String, Object> logo = new HashMap<>();
        List<JSONObject> menuList = DB.select("select * from system_menu where status=?", 1);
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
            menuVO.setStatus(Integer.parseInt(jb.get("status") + ""));
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

    public void menu_list(PrintWriter out) throws SQLException {
        List<JSONObject> data = DB.select("select * from system_menu");
        JSONObject jb = new JSONObject();
        jb.put("code", 0);
        jb.put("msg", "");
        jb.put("count", data.size());
        jb.put("data", data);
        out.print(jb);
    }

    public Map<String, Object> dtree() throws Exception {
        Map<String, Object> map = new LinkedHashMap<>();
        List<JSONObject> menuList = DB.select("select * from system_menu");
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