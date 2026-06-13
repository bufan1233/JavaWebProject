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
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/admin/dashboard")
public class AdminDashboardServlet extends HttpServlet {
    private OrderDao orderDao = new OrderDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 1. 严格的管理员权限拦截
        User user = (User) req.getSession().getAttribute("LOGIN_USER");
        if (user == null || !"ADMIN".equals(user.getRole())) {
            resp.setContentType("text/html;charset=UTF-8");
            resp.getWriter().write("<script>alert('越权访问！这是机密商业数据，请使用管理员账号登录。'); window.location.href='/login.html';</script>");
            return;
        }

        try {
            // 2. 调用我们在上一轮写好的聚合统计方法
            List<Order> stats = orderDao.getSalesStatistics();

            // 3. 召唤 Thymeleaf 进行数据渲染
            ServletContext servletContext = getServletContext();
            TemplateEngine engine = (TemplateEngine) servletContext.getAttribute("templateEngine");
            WebContext ctx = new WebContext(req, resp, servletContext, req.getLocale());

            ctx.setVariable("statsList", stats);
            // 渲染 admin_dashboard.html
            engine.process("admin_dashboard", ctx, resp.getWriter());

        } catch (SQLException e) {
            e.printStackTrace();
            resp.getWriter().write("加载数据大盘失败");
        }
    }
}