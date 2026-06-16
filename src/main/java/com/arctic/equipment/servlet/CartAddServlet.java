package com.arctic.equipment.servlet;

import com.arctic.equipment.entity.Cart;
import com.arctic.equipment.entity.MenuItem;
import com.arctic.equipment.dao.MenuItemDao;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/cart/add")
public class CartAddServlet extends HttpServlet {
    private MenuItemDao menuItemDao = new MenuItemDao();


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html;charset=UTF-8");

        // 1. 获取前端传来的菜品ID和数量
        String itemIdStr = req.getParameter("itemId");
        String countStr = req.getParameter("count");

        if (itemIdStr == null || countStr == null) {
            resp.getWriter().write("<script>alert('参数错误'); history.back();</script>");
            return;
        }

        try {
            Integer itemId = Integer.parseInt(itemIdStr);
            int count = Integer.parseInt(countStr);

            // 2. 从数据库查出该菜品完整信息
            MenuItem menuItem = menuItemDao.findById(itemId);
            if (menuItem == null) {
                resp.getWriter().write("<script>alert('商品不存在'); history.back();</script>");
                return;
            }

            // 3. 从 Session 中获取购物车，没有则创建
            HttpSession session = req.getSession();
            Cart cart = (Cart) session.getAttribute("CART");
            if (cart == null) {
                cart = new Cart();
                session.setAttribute("CART", cart);
            }

            // 4. 将商品加入购物车，返回SUCCESS给前端fetch
            cart.add(menuItem, count);
            resp.getWriter().write("SUCCESS");

        } catch (Exception e) {
            e.printStackTrace();
            resp.getWriter().write("添加购物车异常：" + e.getMessage());
        }
    }
}