package cn.example.blog.servlet.admin;

import cn.example.blog.bean.Dtree;
import cn.example.blog.util.DB;
import cn.example.blog.util.DtreeUtil;
import cn.example.blog.util.GetJsonUtil;
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
import java.util.*;

@WebServlet("/admin/article")
public class ArticleServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
      Map reqMap= GetJsonUtil.toMap(req,resp);
        PrintWriter out = resp.getWriter();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date=new Date();
        String action=reqMap.get("action").toString().trim();
        String id="";
        String cid="";
        String title="";
        String content="";
        switch (action){
            case "edit":
                id=reqMap.get("id").toString().trim();
                 cid=reqMap.get("cid").toString().trim();
                 title=reqMap.get("title").toString().trim();
                 content=reqMap.get("content").toString().trim();
                  try {
                    int count=DB.count("select * from class");
                    if (count==0){
                        out.print("没有分类请先创建分类");
                        return;
                    }else if (cid.equals("0")){
                        out.print("请选择分类");
                        return;
                    }else if (title.trim().equals("")){
                        out.print("标题不能为空");
                        return;
                    }
                    int up=DB.update("update article set cid=?,title=?,content=?,create_time=? where id=?",cid,title,content,date,id);
                    out.print("ok");
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
                break;
            case "getArticle":
                id=reqMap.get("id").toString();
                try {
                    String sql="select *,class.title as class_name from article left join class on article.cid=class.id where article.id=?";
                  ArrayList<JSONObject> res=DB.select(sql,id);
                out.print(res.get(0));
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
                break;
            case "add":
                cid=reqMap.get("cid").toString().trim();
                 title=reqMap.get("title").toString().trim();
                content=reqMap.get("content").toString().trim();
                try {
                    int count=DB.count("select * from class");
                    if (count==0){
                        out.print("没有分类请先创建分类");
                        return;
                    }else if (cid.equals("0")){
                        out.print("请选择分类");
                        return;
                    }else if (title.trim().equals("")){
                        out.print("标题不能为空");
                        return;
                    }
                    int add=DB.insert("insert into article(cid,title,content,create_time)values(?,?,?,?)",cid,title,content,date);
                out.print("ok");
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
                break;
            case "status":
                 id=reqMap.get("id").toString().trim();
                String status=reqMap.get("status").toString().trim();
               if (status.equals("0.0")){
                   status="0";
               }else if (status.equals("1.0")){
                   status="1";
               }
                try {
                    int up=DB.update("update article set status=? where id=?",status,id);
                    out.print("ok");
                } catch (SQLException throwables) {
                    out.print("操作失败,请重试");
                    throwables.printStackTrace();
                }
                break;
            case "delete":
                id=reqMap.get("id").toString().trim();
                try {
                    int del=DB.delete("delete from article where id=?",id);
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
         Map<String, String[]> reqMap=req.getParameterMap();
        String action=reqMap.get("action")[0];
         /* Enumeration<String> parameter= req.getParameterNames();
        while (parameter.hasMoreElements()){
            System.out.print(parameter.nextElement()+"\n");
        }*/
        switch (action){
            case "list":
                int page=Integer.parseInt(reqMap.get("page")[0]);
                int limit=Integer.parseInt(reqMap.get("limit")[0]);
                 try {
                    dataList(out,page,limit);
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
                break;
            case "dtree":
                try {
                    Map<String, Object> dtree = this.dtree();
                    Gson gson = new Gson();
                    String dtreeJson = gson.toJson(dtree);
                    out.print(dtreeJson);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }
    public void dataList(PrintWriter out,int page,int limit) throws SQLException {
        List<JSONObject> data= DB.select("select article.*,class.title as class_name from article left join class on article.cid=class.id order by id desc limit ?,?",(page-1)*limit,limit);
        Map map=new HashMap();
        map.put("code",0);
        map.put("msg","");
        map.put("count",data.size());
        map.put("data",data);
        Gson gson=new Gson();
        String json=gson.toJson(map);
        out.print(json);
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
}
