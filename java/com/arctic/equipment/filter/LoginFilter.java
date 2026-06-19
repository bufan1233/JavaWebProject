package com.arctic.equipment.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.arctic.equipment.entity.User;

// 只拦截 /order/ 开头的私密路径，不拦截 /login 和公共主页
// 通过 web.xml 配置，保证在 RateLimitFilter 之后执行
public class LoginFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("LOGIN_USER");

        if (user == null) {
            // 没有 Session，说明未登录或已过期，直接踢回登录页
            resp.getWriter().write("<script>alert('您尚未登录，请先登录！'); window.location.href='" + req.getContextPath() + "/login.html';</script>");
            return; // 必须 return，阻断执行
        }

        // 有通行证，放行
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {}
}