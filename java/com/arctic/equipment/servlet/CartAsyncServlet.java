package com.arctic.equipment.servlet;

import java.io.IOException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.arctic.equipment.entity.Cart;

@WebServlet("/cart/ajaxUpdate")
public class CartAsyncServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        HttpSession session = req.getSession();
        Cart cart = (Cart) session.getAttribute("CART");
        if (cart == null) {
            resp.getWriter().write("{\"success\":false}");
            return;
        }

        try {
            Integer itemId = Integer.parseInt(req.getParameter("itemId"));
            int count = Integer.parseInt(req.getParameter("count"));

            // 更新前记录单项总价（用于判断是否被移除后前端隐藏）
            String itemTotalPrice = "0";
            boolean existed = false;
            for (Cart.CartItem item : cart.getItems()) {
                if (item.getMenuItem().getId().equals(itemId)) {
                    existed = true;
                    break;
                }
            }

            // 更新本地购物车项
            cart.updateCount(itemId, count);

            // 查找更新后的单项总价
            for (Cart.CartItem item : cart.getItems()) {
                if (item.getMenuItem().getId().equals(itemId)) {
                    itemTotalPrice = item.getItemTotalPrice().toString();
                    break;
                }
            }

            // 返回最新计算的单项总价、购物车总金额、总件数（拼接为 JSON）
            String json = String.format(
                    "{\"success\":true, \"itemId\":%d, \"newCount\":%d, \"itemTotalPrice\":%s, \"totalMoney\":%s, \"cartSize\":%d}",
                    itemId, count, itemTotalPrice,
                    cart.getTotalMoney().toString(),
                    cart.getItems().size()
            );
            resp.getWriter().write(json);
        } catch (Exception e) {
            resp.getWriter().write("{\"success\":false}");
        }
    }
}