package com.arctic.equipment.servlet;

import com.arctic.equipment.dao.OrderDao;
import com.arctic.equipment.entity.Order;
import com.arctic.equipment.entity.User;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/order/my")
public class MyOrderServlet extends HttpServlet {
    private OrderDao orderDao = new OrderDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("LOGIN_USER");

        // 由于有 LoginFilter 保护，走到这里 user 绝对不为空
        try {
            List<Order> orders = orderDao.findOrdersByUserId(user.getId());

            ServletContext servletContext = getServletContext();
            TemplateEngine engine = (TemplateEngine) servletContext.getAttribute("templateEngine");
            WebContext ctx = new WebContext(req, resp, servletContext, req.getLocale());

            ctx.setVariable("orderList", orders);
            // 渲染并返回 order_list.html
            engine.process("order_list", ctx, resp.getWriter());

        } catch (SQLException e) {
            e.printStackTrace();
            resp.getWriter().write("加载订单历史失败");
        }
    }
}