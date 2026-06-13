package com.arctic.equipment.dao;

import com.arctic.equipment.entity.User;
import com.arctic.equipment.util.DBUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDao {
    // 验证账号密码
    public User login(String username, String password) throws SQLException {
        Connection conn = DBUtil.getConnection();
        String sql = "SELECT id, username, role FROM user WHERE username = ? AND password = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    user.setId(rs.getInt("id"));
                    user.setUsername(rs.getString("username"));
                    user.setRole(rs.getString("role"));
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
}