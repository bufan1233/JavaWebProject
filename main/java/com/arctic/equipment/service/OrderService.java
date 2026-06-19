package com.arctic.equipment.service;

import com.arctic.equipment.dao.MenuItemDao;
import com.arctic.equipment.dao.OrderDao;
import com.arctic.equipment.dao.UserDao;
import com.arctic.equipment.entity.Cart;
import com.arctic.equipment.util.DBUtil;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;

public class OrderService {

    private MenuItemDao menuItemDao = new MenuItemDao();
    private OrderDao orderDao = new OrderDao();
    private UserDao userDao = new UserDao();

    /**
     * 业务一：单品直接下单（由 OrderServlet 调用）
     */
    public String processOrder(Integer userId, Integer itemId, int count) {
        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false);

            var item = menuItemDao.findAll().stream().filter(i -> i.getId().equals(itemId)).findFirst().orElse(null);
            if (item == null) {
                conn.rollback();
                return "FAIL: 菜品不存在！";
            }
            BigDecimal totalAmount = item.getPrice().multiply(new BigDecimal(count));

            // 1. 扣减用户余额（传入负数）
            int userAffected = userDao.updateBalance(userId, totalAmount.negate());
            if (userAffected == 0) {
                conn.rollback();
                return "FAIL: 您的钱包余额不足，请先充值！";
            }

            // 2. 扣减库存
            int affectedRows = menuItemDao.decreaseStock(itemId, count);
            if (affectedRows == 0) {
                conn.rollback();
                return "FAIL: 手慢了，库存不足！";
            }

            // 3. 记录订单
            orderDao.insertOrder(userId, itemId, count);

            conn.commit();
            return "SUCCESS";

        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            e.printStackTrace();
            return "ERROR: 系统繁忙，请稍后再试";
        }
    }

    /**
     * 业务二：购物车批量下单（CartPayServlet 调用）
     */
    public String processCartOrder(Integer userId, Cart cart) {
        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false);

            for (Cart.CartItem item : cart.getItems()) {
                Integer itemId = item.getMenuItem().getId();
                int count = item.getCount();
                BigDecimal itemTotal = item.getMenuItem().getPrice().multiply(new BigDecimal(count));

                // 1. 扣减用户余额
                int userAffected = userDao.updateBalance(userId, itemTotal.negate());
                if (userAffected == 0) {
                    conn.rollback();
                    return "FAIL: 您的钱包余额不足，请先充值！";
                }

                // 2. 扣减库存
                int affectedRows = menuItemDao.decreaseStock(itemId, count);
                if (affectedRows == 0) {
                    conn.rollback();
                    return "FAIL: 菜品库存不足！";
                }

                // 3. 记录订单
                orderDao.insertOrder(userId, itemId, count);
            }
            conn.commit();
            return "SUCCESS: 下单成功！";
        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            e.printStackTrace();
            return "ERROR: 系统繁忙";
        }
    }
}