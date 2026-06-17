package com.arctic.equipment.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import com.arctic.equipment.util.DBUtil;

// 拦截所有请求 /* 
// 通过 web.xml 配置，确保在所有 Filter 中最先执行
public class EncodingFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        // 强制设置所有进出的数据都为 UTF-8
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        try {
            // 放行，让请求继续往后走
            chain.doFilter(request, response);
        } finally {
            // 无论请求处理成功与否，都归还数据库连接到连接池
            DBUtil.closeConnection();
        }
    }

    @Override
    public void destroy() {}
}
