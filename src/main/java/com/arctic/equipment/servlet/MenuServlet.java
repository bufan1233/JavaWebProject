package com.arctic.equipment.servlet;

import com.arctic.equipment.dao.MenuItemDao;
import com.arctic.equipment.entity.MenuItem;
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

@WebServlet("/menu")
public class MenuServlet extends HttpServlet {
    private MenuItemDao menuItemDao = new MenuItemDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            // 1. 获取所有菜品
            List<MenuItem> items = menuItemDao.findAll();

            // 2. 调用 Thymeleaf 引擎进行页面渲染
            ServletContext servletContext = getServletContext();
            TemplateEngine engine = (TemplateEngine) servletContext.getAttribute("templateEngine");

            // 3. 将数据塞入上下文（类似将数据打包寄给前端）
            WebContext ctx = new WebContext(req, resp, servletContext, req.getLocale());
            ctx.setVariable("menuList", items);

            // 4. 渲染 WEB-INF/pages/menu.html 并返回给浏览器
            engine.process("menu", ctx, resp.getWriter());

        } catch (SQLException e) {
            e.printStackTrace();
            resp.getWriter().write("加载菜单失败");
        }
    }
}