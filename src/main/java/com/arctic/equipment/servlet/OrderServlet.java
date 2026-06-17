package com.arctic.equipment.servlet;

import com.arctic.equipment.entity.User;
import com.arctic.equipment.service.OrderService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/order/create")
public class OrderServlet extends HttpServlet {

    private OrderService orderService = new OrderService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        HttpSession session = request.getSession();

        User currentUser = (User) session.getAttribute("LOGIN_USER");
        if (currentUser == null) {
            response.getWriter().write("<script>alert('请先登录！'); window.location.href='" + request.getContextPath() + "/login.html';</script>");
            return;
        }

        String itemIdStr = request.getParameter("itemId");
        String countStr = request.getParameter("count");

        try {
            Integer itemId = Integer.parseInt(itemIdStr);
            int count = Integer.parseInt(countStr);

            String result = orderService.processOrder(currentUser.getId(), itemId, count);

            if (result.equals("SUCCESS")) {
                // 刷新 Session 中的用户余额
                try {
                    com.arctic.equipment.dao.UserDao userDao = new com.arctic.equipment.dao.UserDao();
                    User freshUser = userDao.findById(currentUser.getId());
                    if (freshUser != null) {
                        session.setAttribute("LOGIN_USER", freshUser);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                response.sendRedirect(request.getContextPath() + "/menu?success=true");
            } else {
                // 打印 Service 传回的错误原因（如：余额不足 / 库存不足）
                response.getWriter().write("<script>alert('" + result + "'); window.history.back();</script>");
            }
        } catch (NumberFormatException e) {
            response.getWriter().write("<script>alert('非法参数！'); window.history.back();</script>");
        }
    }
}
