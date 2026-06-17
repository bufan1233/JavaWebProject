package com.arctic.equipment.servlet;

import com.arctic.equipment.dao.MenuItemDao;
import com.arctic.equipment.entity.MenuItem;
import com.arctic.equipment.entity.User;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;

@WebServlet("/admin/menu/save")
public class AdminMenuServlet extends HttpServlet {
    private MenuItemDao menuItemDao = new MenuItemDao();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // 1. 权限拦截
        com.arctic.equipment.entity.User user = (com.arctic.equipment.entity.User) req.getSession().getAttribute("LOGIN_USER");
        if (user == null || !"ADMIN".equals(user.getRole())) {
            resp.getWriter().write("越权操作！");
            return;
        }

        // 2. 接收多维参数
        String name = req.getParameter("name");
        java.math.BigDecimal price = new java.math.BigDecimal(req.getParameter("price"));
        int stock = Integer.parseInt(req.getParameter("stock"));

        try {
            // 3. 调用双重去重校验
            com.arctic.equipment.entity.MenuItem existItem = menuItemDao.findByNameAndPrice(name, price);

            resp.setContentType("text/html;charset=UTF-8");
            if (existItem != null) {
                // 只有名字和价格完全一样，才在原纪录上叠加库存
                menuItemDao.addStock(existItem.getId(), stock);
                resp.getWriter().write("<script>alert('相同价格的相同菜品已存在，已成功叠加库存！'); history.back();</script>");
            } else {
                // 只要价格不同，或者名字不同，都作为一条全新的独立记录插入
                menuItemDao.addMenuItem(name, price, stock);
                resp.getWriter().write("<script>alert('新菜品/新定价录入成功！'); history.back();</script>");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            resp.getWriter().write("系统异常");
        }
    }
}