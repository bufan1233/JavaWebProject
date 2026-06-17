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

@WebServlet("/user/changePassword")
public class ChangePasswordServlet extends HttpServlet {
    private UserDao userDao = new UserDao();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/plain;charset=UTF-8");

        HttpSession session = req.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("LOGIN_USER") : null;

        if (user == null) {
            resp.getWriter().write("NEED_LOGIN");
            return;
        }

        String oldPassword = req.getParameter("oldPassword");
        String newPassword = req.getParameter("newPassword");

        if (oldPassword == null || oldPassword.trim().isEmpty() || newPassword == null || newPassword.trim().isEmpty()) {
            resp.getWriter().write("密码不能为空");
            return;
        }

        try {
            boolean success = userDao.updatePassword(user.getId(), oldPassword, newPassword);
            if (success) {
                resp.getWriter().write("SUCCESS");
            } else {
                resp.getWriter().write("旧密码不正确，请重新输入");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            resp.getWriter().write("系统异常，请稍后重试");
        }
    }
}