package com.arctic.equipment.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.Queue;

/**
 * 基于等待-唤醒机制的手写数据库连接池
 * 
 * 核心原理：
 *   生产者-消费者模型：Servlet 请求作为消费者 borrow 连接，请求结束后作为生产者 return 连接。
 *   当池中无可用连接时，消费者线程调用 wait() 挂起等待；
 *   当有连接归还时，通过 notifyAll() 唤醒所有等待线程竞争获取。
 * 
 * 容量设计：
 *   最大连接数 10，应用启动时通过 ServletContextListener 预初始化。
 */
public class ConnectionPool {

    /** 连接池最大容量 */
    private static final int MAX_POOL_SIZE = 10;

    /** 数据库连接信息 */
    private static final String URL = "jdbc:mysql://localhost:3306/order_system?useSSL=false&serverTimezone=UTC&characterEncoding=UTF-8&allowPublicKeyRetrieval=true";
    private static final String USER = "root";
    private static final String PASSWORD = "zf20051122";

    /** 连接队列 —— 使用 LinkedList 作为 FIFO 队列，公平调度 */
    private static final Queue<Connection> pool = new LinkedList<>();

    /** 初始化标志，防止重复初始化 */
    private static volatile boolean initialized = false;

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("数据库驱动加载失败", e);
        }
    }

    /**
     * 初始化连接池：预创建 MAX_POOL_SIZE 个连接放入队列
     * 应在 ServletContextListener#contextInitialized 中调用
     */
    public static synchronized void init() {
        if (initialized) {
            return;
        }
        try {
            for (int i = 0; i < MAX_POOL_SIZE; i++) {
                Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
                pool.offer(conn);
            }
            initialized = true;
            System.out.println("====== ConnectionPool 初始化完成，池大小: " + MAX_POOL_SIZE + " ======");
        } catch (SQLException e) {
            throw new RuntimeException("连接池初始化失败", e);
        }
    }

    /**
     * 借出一个数据库连接
     * 
     * 并发语义：
     *   如果池为空，当前线程进入 WAITING 状态，直到有其他线程归还连接并调用 notifyAll()
     *   使用 while 而非 if 防止虚假唤醒（spurious wakeup）
     * 
     * @return 一个可用的数据库连接
     * @throws SQLException 如果等待过程中线程被中断或获取连接失败
     */
    public static Connection borrow() throws SQLException {
        synchronized (pool) {
            // 轮询等待直到有可用连接 —— while 防御虚假唤醒
            while (pool.isEmpty()) {
                try {
                    pool.wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new SQLException("等待数据库连接时线程被中断", e);
                }
            }
            Connection conn = pool.poll();
            // 二次校验：归还时已做过有效性检查，此处再验一次兜底
            if (conn != null && conn.isClosed()) {
                // 连接已关闭（极端情况），递归获取新连接
                conn = DriverManager.getConnection(URL, USER, PASSWORD);
            }
            return conn;
        }
    }

    /**
     * 归还数据库连接
     * 
     * 并发语义：
     *   归还后调用 notifyAll() 唤醒所有等待 borrow() 的线程
     *   归还前校验连接有效性，无效则创建新连接补充入池
     * 
     * @param conn 要归还的连接，应为 null-safe 调用
     */
    public static void returnConnection(Connection conn) {
        if (conn == null) {
            return;
        }
        synchronized (pool) {
            try {
                if (!conn.isClosed()) {
                    pool.offer(conn);
                } else {
                    // 连接已关闭，创建新连接补充
                    Connection newConn = DriverManager.getConnection(URL, USER, PASSWORD);
                    pool.offer(newConn);
                    System.out.println("====== ConnectionPool: 检测到关闭连接，已创建新连接补充 ======");
                }
            } catch (SQLException e) {
                // 归还失败（极端异常），尝试补充一个新连接到池中
                try {
                    Connection newConn = DriverManager.getConnection(URL, USER, PASSWORD);
                    pool.offer(newConn);
                } catch (SQLException ex) {
                    System.err.println("====== ConnectionPool: 归还连接失败且无法补充新连接 ======");
                    ex.printStackTrace(System.err);
                }
            } finally {
                pool.notifyAll(); // 唤醒所有等待线程
            }
        }
    }

    /**
     * 销毁连接池：关闭池中所有连接
     * 应在 ServletContextListener#contextDestroyed 中调用
     */
    public static synchronized void destroy() {
        synchronized (pool) {
            while (!pool.isEmpty()) {
                Connection conn = pool.poll();
                if (conn != null) {
                    try {
                        if (!conn.isClosed()) {
                            conn.close();
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
            System.out.println("====== ConnectionPool 已销毁，所有连接已关闭 ======");
        }
        initialized = false;
    }

    /**
     * 获取当前池中可用连接数（用于监控/调试）
     */
    public static int getAvailableCount() {
        synchronized (pool) {
            return pool.size();
        }
    }
}