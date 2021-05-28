package cn.example.blog.util;

import com.google.gson.Gson;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

public class GetJsonUtil {
    public static Map toMap(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/html;charset=utf-8");
        InputStream in = req.getInputStream();
        BufferedReader bin = new BufferedReader(new InputStreamReader(in, "UTF-8"));
        String line = null;
        StringBuffer content = new StringBuffer();
        while ((line = bin.readLine()) != null) {
            content.append(line);
        }
        Gson gson = new Gson();
        Map map = gson.fromJson(content + "", Map.class);
        return map;
    }
}
