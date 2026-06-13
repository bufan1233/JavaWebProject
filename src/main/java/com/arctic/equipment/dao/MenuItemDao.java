package com.arctic.equipment.dao;

import com.arctic.equipment.entity.MenuItem;
import com.arctic.equipment.util.DBUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MenuItemDao {

    /**
     扣减库存 (并发安全版：乐观锁思想)
     @param itemId 菜品ID
     @param count 扣减数量
     @return 影响的行数。返回 0 代表库存不足。
     **/
    public int decreaseStock(Integer itemId, int count) throws SQLException {
        // 从 DBUtil 获取当前线程的连接
        Connection conn = DBUtil.getConnection();

        // 核心 SQL：在更新的同时检查库存是否足够
        String sql = "UPDATE menu_item SET stock = stock - ? WHERE id = ? AND stock >= ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, count);
            pstmt.setInt(2, itemId);
            pstmt.setInt(3, count);

            // 返回受影响的行数
            return pstmt.executeUpdate();
        }
    }
    public java.util.List<com.arctic.equipment.entity.MenuItem> findAll() throws SQLException {
        java.util.List<com.arctic.equipment.entity.MenuItem> list = new java.util.ArrayList<>();
        Connection conn = DBUtil.getConnection();
        String sql = "SELECT * FROM menu_item";
        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             java.sql.ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                com.arctic.equipment.entity.MenuItem item = new com.arctic.equipment.entity.MenuItem();
                item.setId(rs.getInt("id"));
                item.setName(rs.getString("name"));
                item.setPrice(rs.getBigDecimal("price"));
                item.setStock(rs.getInt("stock"));
                list.add(item);
            }
        }
        return list;
    }
    public com.arctic.equipment.entity.MenuItem findByNameAndPrice(String name, java.math.BigDecimal price) throws SQLException {
        Connection conn = DBUtil.getConnection();
        // 核心 SQL：引入 price 作为联合排重条件
        String sql = "SELECT * FROM menu_item WHERE name = ? AND price = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setBigDecimal(2, price);
            try (java.sql.ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    com.arctic.equipment.entity.MenuItem item = new com.arctic.equipment.entity.MenuItem();
                    item.setId(rs.getInt("id"));
                    item.setName(rs.getString("name"));
                    item.setPrice(rs.getBigDecimal("price"));
                    item.setStock(rs.getInt("stock"));
                    return item;
                }
            }
        }
        return null;
    }

    // 管理员：新增菜品
    public void addMenuItem(String name, java.math.BigDecimal price, int stock) throws SQLException {
        Connection conn = DBUtil.getConnection();
        String sql = "INSERT INTO menu_item (name, price, stock) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setBigDecimal(2, price);
            pstmt.setInt(3, stock);
            pstmt.executeUpdate();
        }
    }

    // 管理员：增加库存（追加模式）
    public void addStock(Integer id, int addCount) throws SQLException {
        Connection conn = DBUtil.getConnection();
        String sql = "UPDATE menu_item SET stock = stock + ? WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, addCount);
            pstmt.setInt(2, id);
            pstmt.executeUpdate();
        }
    }
}