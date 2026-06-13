package com.arctic.equipment.service;

import com.arctic.equipment.dao.MenuItemDao;
import com.arctic.equipment.dao.OrderDao;
import com.arctic.equipment.util.DBUtil;

import java.sql.Connection;
import java.sql.SQLException;

public class OrderService {

    private MenuItemDao menuItemDao = new MenuItemDao();
    private OrderDao orderDao = new OrderDao(); // 解封：引入 OrderDao

    public String processOrder(Integer userId, Integer itemId, int count) {
        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false); // 开启事务

            // 1. 扣减库存 (乐观锁)
            int affectedRows = menuItemDao.decreaseStock(itemId, count);
            if (affectedRows == 0) {
                conn.rollback();
                return "FAIL: 手慢了，库存不足！";
            }

            // 2. 记录订单 (解封：真正写入订单表)
            orderDao.insertOrder(userId, itemId, count);

            conn.commit(); // 全部成功，提交事务
            return "SUCCESS: 下单成功！";

        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            e.printStackTrace();
            return "ERROR: 系统繁忙，请稍后再试";
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); } catch (SQLException e) { e.printStackTrace(); }
            }
            DBUtil.closeConnection();
        }
    }
}