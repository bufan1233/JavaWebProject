package com.arctic.equipment.servlet;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import com.arctic.equipment.dao.MenuItemDao;
import com.arctic.equipment.entity.Cart;
import com.arctic.equipment.entity.MenuItem;

@WebServlet("/menu")
public class MenuServlet extends HttpServlet {
    private MenuItemDao menuItemDao = new MenuItemDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html;charset=UTF-8");
        try {
            // 1. 获取所有菜品
            List<MenuItem> items = menuItemDao.findAll();

            // 分离出"爆款热销"和"精选饮品"
            List<MenuItem> hotItems = new java.util.ArrayList<>();
            List<MenuItem> drinkItems = new java.util.ArrayList<>();
            for (MenuItem item : items) {
                if ("热销".equals(item.getCategory())) {
                    hotItems.add(item);
                } else if ("饮品".equals(item.getCategory())) {
                    drinkItems.add(item);
                }
            }

            // 2. 调用 Thymeleaf 引擎进行页面渲染
            ServletContext servletContext = getServletContext();
            TemplateEngine engine = (TemplateEngine) servletContext.getAttribute("templateEngine");

            if (engine == null) {
                resp.getWriter().write("<script>alert('系统初始化失败，请联系管理员。错误：TemplateEngine 未初始化'); window.location.href='" + req.getContextPath() + "/login.html';</script>");
                return;
            }

            // 3. 将数据塞入上下文（类似将数据打包寄给前端）
            WebContext ctx = new WebContext(req, resp, servletContext, req.getLocale());
            ctx.setVariable("menuList", items);
            ctx.setVariable("hotItems", hotItems);
            ctx.setVariable("drinkItems", drinkItems);

            // ====== 新增：把 Session 中的购物车对象同步传给 Thymeleaf 模板 ======
            HttpSession session = req.getSession();
            Cart cart = (Cart) session.getAttribute("CART");
            if (cart == null) {
                cart = new Cart();
                session.setAttribute("CART", cart);
            }
            ctx.setVariable("cart", cart);

            // 4. 渲染 WEB-INF/pages/menu.html 并返回给浏览器
            engine.process("menu", ctx, resp.getWriter());
        } catch (SQLException e) {
            e.printStackTrace();
            resp.getWriter().write("<script>alert('数据库异常，加载菜单失败'); window.location.href='" + req.getContextPath() + "/login.html';</script>");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("=== MenuServlet 严重错误 ===");
            e.printStackTrace(System.err);
            try {
                resp.getWriter().write("<script>alert('系统异常，加载菜单失败：" + e.getMessage() + "'); window.location.href='" + req.getContextPath() + "/login.html';</script>");
            } catch (IllegalStateException ex) {
                // 如果 response 已被 commit，记录日志
                System.err.println("Response 已提交，无法写入错误信息");
            }
        }
    }
}