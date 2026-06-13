package com.arctic.equipment.dao;

import com.arctic.equipment.entity.Order;
import com.arctic.equipment.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OrderDao {

    /**
     * 补全的 INSERT 逻辑：将订单数据真正落盘
     */
    public void insertOrder(Integer userId, Integer itemId, int count) throws SQLException {
        // 核心规律：必须从 DBUtil 获取当前线程的 Connection，保证与 MenuItemDao 处于同一事务中！
        Connection conn = DBUtil.getConnection();
        String sql = "INSERT INTO orders (user_id, item_id, quantity) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, itemId);
            pstmt.setInt(3, count);
            pstmt.executeUpdate();
        }
    }

    /**
     * 新增的 SELECT 业务逻辑：查询某用户的所有历史订单 (包含联表查询查出菜名)
     */
    public List<Order> findOrdersByUserId(Integer userId) throws SQLException {
        List<Order> list = new ArrayList<>();
        Connection conn = DBUtil.getConnection();

        // SQL 联表查询，将 orders 表和 menu_item 表结合
        String sql = "SELECT o.id, o.quantity, o.order_time, o.status, m.name AS item_name, m.price AS item_price " +
                "FROM orders o JOIN menu_item m ON o.item_id = m.id " +
                "WHERE o.user_id = ? ORDER BY o.order_time DESC";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Order order = new Order();
                    order.setId(rs.getInt("id"));
                    order.setQuantity(rs.getInt("quantity"));
                    order.setOrderTime(rs.getTimestamp("order_time"));
                    order.setStatus(rs.getString("status"));
                    order.setItemName(rs.getString("item_name"));
                    order.setItemPrice(rs.getBigDecimal("item_price"));
                    list.add(order);
                }
            }
        }
        return list;
    }
    public List<Order> getSalesStatistics() throws SQLException {
        List<Order> list = new ArrayList<>();
        Connection conn = DBUtil.getConnection();
        // 核心 SQL 更新：计算总额 (SUM(o.quantity * m.price)) 并按其降序排序
        String sql = "SELECT m.name AS item_name, m.price AS item_price, SUM(o.quantity) AS total_sales, " +
                "SUM(o.quantity * m.price) AS total_revenue " + // 新增：销售额计算
                "FROM orders o JOIN menu_item m ON o.item_id = m.id " +
                "GROUP BY m.name, m.price " +
                "ORDER BY total_revenue DESC"; // 修改：按销售额排序
        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Order data = new Order();
                data.setItemName(rs.getString("item_name"));
                data.setItemPrice(rs.getBigDecimal("item_price"));
                data.setQuantity(rs.getInt("total_sales"));
                // 注意：这里我们利用 Order 对象的 status 字段临时存放销售额字符串，或者你可以给 Order 类增加 revenue 字段
                data.setStatus(rs.getString("total_revenue"));
                list.add(data);
            }
        }
        return list;
    }
}