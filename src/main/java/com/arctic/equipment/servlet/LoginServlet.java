package com.arctic.equipment.servlet;

import com.arctic.equipment.dao.UserDao;
import com.arctic.equipment.entity.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private UserDao userDao = new UserDao();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String uname = req.getParameter("username");
        String pword = req.getParameter("password");

        try {
            User user = userDao.login(uname, pword);
            if (user != null) {
                // 核心逻辑：给用户发放 Session 通行证！
                HttpSession session = req.getSession();
                session.setAttribute("LOGIN_USER", user);
                // 登录成功，跳转到菜单页
                resp.sendRedirect("/menu");
            } else {
                resp.getWriter().write("用户名或密码错误");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            resp.getWriter().write("系统异常");
        }
    }
}