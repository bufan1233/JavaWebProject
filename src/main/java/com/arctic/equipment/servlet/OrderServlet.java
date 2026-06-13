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

// 使用注解配置路由，无需在 web.xml 中繁琐配置
@WebServlet("/order/create")
public class OrderServlet extends HttpServlet {

    private OrderService orderService = new OrderService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 1. 设置响应编码 (防御性编程，防止中文乱码)
        response.setContentType("text/html;charset=UTF-8");

        // 2. 从 Session 中获取当前登录用户 (这就是 Session 维持状态的应用)
        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("LOGIN_USER");

        if (currentUser == null) {
            response.getWriter().write("请先登录！");
            return; // 阻断后续逻辑
        }

        // 3. 获取前端表单传来的参数 (需要做非空和类型校验)
        String itemIdStr = request.getParameter("itemId");
        String countStr = request.getParameter("count");

        try {
            Integer itemId = Integer.parseInt(itemIdStr);
            int count = Integer.parseInt(countStr);

            // 4. 召唤 Service 执行核心交易链路
            String result = orderService.processOrder(currentUser.getId(), itemId, count);

            // 5. 完善的跳转逻辑（不再是输出纯文本）
            if (result.startsWith("SUCCESS")) {
                // 如果成功，让浏览器重新发送 GET 请求去获取最新的订单列表
                response.sendRedirect("/menu?success=true");
            } else {
                // 如果失败（比如超卖），利用 JS 弹窗提示，并退回菜单页
                response.setContentType("text/html;charset=UTF-8");
                response.getWriter().write("<script>alert('" + result + "'); window.location.href='/menu';</script>");
            }

        } catch (NumberFormatException e) {
            // 防御非法数据输入
            response.getWriter().write("非法的数据格式！");
        }
    }
}