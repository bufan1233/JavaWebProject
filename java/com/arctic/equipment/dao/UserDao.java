package com.arctic.equipment.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.arctic.equipment.entity.User;
import com.arctic.equipment.util.DBUtil;

public class UserDao {
    // 验证账号密码
    public User login(String username, String password) throws SQLException {
        Connection conn = DBUtil.getConnection();
        String sql = "SELECT id, username, role, balance FROM user WHERE username = ? AND password = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    user.setId(rs.getInt("id"));
                    user.setUsername(rs.getString("username"));
                    user.setRole(rs.getString("role"));
                    user.setBalance(rs.getBigDecimal("balance"));
                    return user;
                }
            }
        }
        return null;
    }

    // 检查用户名是否存在
    public boolean checkUserExists(String username) throws SQLException {
        Connection conn = DBUtil.getConnection();
        String sql = "SELECT id FROM user WHERE username = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next(); // 存在返回 true
            }
        }
    }

    public int updateBalance(Integer userId, BigDecimal amount) throws SQLException {
        Connection conn = DBUtil.getConnection();
        String sql = "UPDATE user SET balance = balance + ? WHERE id = ? AND (balance + ?) >= 0";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setBigDecimal(1, amount);
            pstmt.setInt(2, userId);
            pstmt.setBigDecimal(3, amount);
            return pstmt.executeUpdate();
        }
    }

    // 修改密码：先验证旧密码是否匹配，再更新为新密码
    public boolean updatePassword(Integer userId, String oldPassword, String newPassword) throws SQLException {
        Connection conn = DBUtil.getConnection();
        // 先验证旧密码是否正确
        String checkSql = "SELECT id FROM user WHERE id = ? AND password = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(checkSql)) {
            pstmt.setInt(1, userId);
            pstmt.setString(2, oldPassword);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (!rs.next()) {
                    return false; // 旧密码不匹配
                }
            }
        }
        // 更新为新密码
        String updateSql = "UPDATE user SET password = ? WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(updateSql)) {
            pstmt.setString(1, newPassword);
            pstmt.setInt(2, userId);
            return pstmt.executeUpdate() > 0;
        }
    }

    // 注册新用户
    public void register(String username, String password, String role) throws SQLException {
        Connection conn = DBUtil.getConnection();
        String sql = "INSERT INTO user (username, password, role) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.setString(3, role);
            pstmt.executeUpdate();
        }
    }

    // 根据 ID 查询用户（含余额）
    public User findById(Integer id) throws SQLException {
        Connection conn = DBUtil.getConnection();
        String sql = "SELECT id, username, role, balance FROM user WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    user.setId(rs.getInt("id"));
                    user.setUsername(rs.getString("username"));
                    user.setRole(rs.getString("role"));
                    user.setBalance(rs.getBigDecimal("balance"));

                    return user;
                }
            }
        }
        return null;
    }
}