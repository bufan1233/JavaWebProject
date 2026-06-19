package com.arctic.equipment.service;

import com.arctic.equipment.dao.UserDao;
import com.arctic.equipment.util.DBUtil;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;

public class UserService {
    private UserDao userDao = new UserDao();

    public boolean recharge(Integer userId, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) return false;
        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false);

            userDao.updateBalance(userId, amount);

            conn.commit();
            return true;
        } catch (SQLException e) {
            if (conn != null) { try { conn.rollback(); } catch (SQLException ex) {} }
            e.printStackTrace();
            return false;
        }
    }
}