package com.arctic.equipment.servlet;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.arctic.equipment.dao.UserDao;
import com.arctic.equipment.entity.User;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private UserDao userDao = new UserDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // GET 请求直接重定向到登录页面
        resp.sendRedirect(req.getContextPath() + "/login.html");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html;charset=UTF-8");
        String uname = req.getParameter("username");
        String pword = req.getParameter("password");

        try {
            User user = userDao.login(uname, pword);
            if (user != null) {
                // 核心逻辑：给用户发放 Session 通行证！
                HttpSession session = req.getSession();
                session.setAttribute("LOGIN_USER", user);
                // 登录成功，跳转到菜单页
                try {
                    resp.sendRedirect(req.getContextPath() + "/menu");
                } catch (IllegalStateException e) {
                    // 如果 response 已经被 commit，用 JS 跳转兜底
                    resp.getWriter().write("<script>window.location.href='" + req.getContextPath() + "/menu';</script>");
                }
            } else {
                resp.getWriter().write("<script>alert('用户名或密码错误'); window.location.href='" + req.getContextPath() + "/login.html';</script>");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            resp.getWriter().write("<script>alert('系统异常，请稍后再试'); window.location.href='" + req.getContextPath() + "/login.html';</script>");
        } catch (Exception e) {
            e.printStackTrace();
            resp.getWriter().write("<script>alert('系统异常：" + e.getMessage() + "'); window.location.href='" + req.getContextPath() + "/login.html';</script>");
        }
    }
}
