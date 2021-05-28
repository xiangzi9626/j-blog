package cn.example.blog.filter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

//@WebFilter("/*")
public class EncodingFilter implements Filter {
    /**
     * 初始化方法,从web.xml中获取配置的初始化参数
     */
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    /**
     * 设置编码
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        httpRequest.setCharacterEncoding("UTF-8");
        chain.doFilter(httpRequest, httpResponse);
    }

    @Override
    public void destroy() {
    }
}