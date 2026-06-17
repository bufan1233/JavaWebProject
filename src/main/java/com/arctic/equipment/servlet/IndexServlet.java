package com.arctic.equipment.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.arctic.equipment.entity.User;

@WebServlet("/index")
public class IndexServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("LOGIN_USER") : null;

        if (user != null) {
            // 已登录，直接进入菜单页
            resp.sendRedirect(req.getContextPath() + "/menu");
        } else {
            // 未登录，跳转登录页
            resp.sendRedirect(req.getContextPath() + "/login.html");
        }
    }
}