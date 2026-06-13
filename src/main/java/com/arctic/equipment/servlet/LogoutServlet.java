package com.arctic.equipment.servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false); // false 表示如果没有 session 就不创建新的
        if (session != null) {
            // 核心逻辑：强制销毁该用户的内存储物柜
            session.invalidate();
        }
        // 重定向回登录页
        resp.sendRedirect("/login.html");
    }
}