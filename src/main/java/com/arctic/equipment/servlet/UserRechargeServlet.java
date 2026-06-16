package com.arctic.equipment.servlet;

import com.arctic.equipment.entity.User;
import com.arctic.equipment.util.DBUtil;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;

@WebServlet("/user/recharge")
public class UserRechargeServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html;charset=UTF-8");

        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("LOGIN_USER");
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login.html");
            return;
        }

        String amountStr = req.getParameter("amount");
        if (amountStr == null || amountStr.trim().isEmpty()) {
            resp.getWriter().write("<script>alert('请输入有效金额'); history.back();</script>");
            return;
        }

        try {
            BigDecimal amount = new BigDecimal(amountStr);
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                resp.getWriter().write("<script>alert('充值金额必须大于0'); history.back();</script>");
                return;
            }

            Connection conn = DBUtil.getConnection();
            String sql = "UPDATE user SET balance = balance + ? WHERE id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setBigDecimal(1, amount);
                pstmt.setInt(2, user.getId());
                pstmt.executeUpdate();
            }

            // 同步更新 Session 中的 User 余额
            if (user.getBalance() == null) {
                user.setBalance(BigDecimal.ZERO);
            }
            user.setBalance(user.getBalance().add(amount));
            session.setAttribute("LOGIN_USER", user);

            resp.getWriter().write("SUCCESS");

        } catch (Exception e) {
            e.printStackTrace();
            resp.getWriter().write("充值失败，系统异常：" + e.getMessage());
        }
    }
}
