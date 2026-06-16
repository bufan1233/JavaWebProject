package com.arctic.equipment.servlet;

import com.arctic.equipment.entity.Cart;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/cart/ajaxUpdate")
public class CartAsyncServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession();
        Cart cart = (Cart) session.getAttribute("CART");
        if (cart == null) {
            resp.getWriter().write("{\"success\":false}");
            return;
        }

        try {
            Integer itemId = Integer.parseInt(req.getParameter("itemId"));
            int count = Integer.parseInt(req.getParameter("count"));

            // 更新本地购物车项
            cart.updateCount(itemId, count);

            // 返回最新计算的单项总价、购物车总金额、总件数（拼接为 JSON）
            String json = String.format(
                    "{\"success\":true, \"totalMoney\":%s, \"cartSize\":%d}",
                    cart.getTotalMoney().toString(),
                    cart.getItems().size()
            );
            resp.getWriter().write(json);
        } catch (Exception e) {
            resp.getWriter().write("{\"success\":false}");
        }
    }
}