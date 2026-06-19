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

public class AdminFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        HttpSession session = req.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("LOGIN_USER") : null;

        if (user == null) {
            // 未登录，弹窗提示并跳转登录页
            resp.setContentType("text/html;charset=UTF-8");
            resp.getWriter().write("<script>alert('您尚未登录，请先登录！'); window.location.href='" + req.getContextPath() + "/login.html';</script>");
            return;
        }

        if (!"ADMIN".equals(user.getRole())) {
            // 不是管理员，弹窗提示并跳转点餐中心
            resp.setContentType("text/html;charset=UTF-8");
            resp.getWriter().write("<script>alert('您没有管理员权限，无法访问该页面！'); window.location.href='" + req.getContextPath() + "/menu';</script>");
            return;
        }

        // 管理员身份验证通过，放行
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {}
}