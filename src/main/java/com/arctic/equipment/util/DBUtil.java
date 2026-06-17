package com.arctic.equipment.util;

import java.sql.Connection;
import java.sql.SQLException;

public class DBUtil {

    // 利用 ThreadLocal 保证同一个线程（同一个HTTP请求）拿到的是同一个 Connection，这是做事务控制的前提
    private static ThreadLocal<Connection> connectionHolder = new ThreadLocal<>();

    /**
     * 获取数据库连接（从连接池中借出）
     * 
     * ThreadLocal 保证同一个线程多次调用 getConnection() 返回同一个连接，
     * 从而确保 DAO 层多个方法调用处于同一事务中。
     */
    public static Connection getConnection() throws SQLException {
        Connection conn = connectionHolder.get();
        if (conn == null || conn.isClosed()) {
            conn = ConnectionPool.borrow(); // 从连接池借出，若池空则 wait() 等待
            connectionHolder.set(conn);
        }
        return conn;
    }

    /**
     * 归还连接并清理 ThreadLocal
     * 
     * 归还连接回到池中（而非物理关闭），唤醒等待 borrow() 的线程。
     * 应在每个 HTTP 请求结束的 finally 块中调用（如 Filter 中）。
     */
    public static void closeConnection() {
        Connection conn = connectionHolder.get();
        if (conn != null) {
            ConnectionPool.returnConnection(conn); // 归还到池中，由连接池管理生命周期
            connectionHolder.remove(); // 防止内存泄漏
        }
    }
}
