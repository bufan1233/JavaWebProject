
SET NAMES utf8mb4;

DROP DATABASE IF EXISTS order_system;
CREATE DATABASE order_system DEFAULT CHARACTER SET utf8mb4;
USE order_system;


DROP TABLE IF EXISTS orders;
DROP TABLE IF EXISTS menu_item;
DROP TABLE IF EXISTS user;

CREATE TABLE IF NOT EXISTS user (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(50) NOT NULL,
    role VARCHAR(20) DEFAULT 'USER' COMMENT 'USER 或 ADMIN',
    balance DECIMAL(10,2) DEFAULT 0.00 COMMENT '用户余额'
);


CREATE TABLE IF NOT EXISTS menu_item (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    stock INT UNSIGNED NOT NULL DEFAULT 0,
    category VARCHAR(20) COMMENT '分类：主食、热销、饮品'
);


CREATE TABLE IF NOT EXISTS orders (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    item_id INT NOT NULL,
    quantity INT NOT NULL,
    order_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(20) DEFAULT 'PAID'
);


INSERT INTO menu_item (name, price, stock, category) VALUES
('招牌牛肉面', 25.00, 50, '主食'),
('限量帝王蟹', 999.00, 1, '主食'),
('秘制烤面筋', 5.00, 100, '主食'),
('经典麻婆豆腐', 18.00, 30, '主食'),
('宫保鸡丁', 22.00, 40, '主食'),
('蒜蓉粉丝扇贝', 35.00, 20, '主食'),
('老北京炸酱面', 16.00, 60, '主食'),
('重庆辣子鸡', 38.00, 15, '主食'),
('清炒时蔬', 12.00, 50, '主食'),
('番茄鸡蛋汤', 10.00, 50, '主食'),
('冰镇酸梅汤', 6.00, 100, '主食'),
('鲜榨西瓜汁', 15.00, 30, '主食'),
('黑松露炒饭', 68.00, 10, '主食'),
('法式香煎鹅肝', 128.00, 5, '主食'),
('香脆炸薯条', 12.00, 80, '主食'),
('芝士爆浆鸡排', 24.00, 25, '主食');


INSERT INTO menu_item (name, price, stock, category) VALUES
('火焰牛排铁板烧', 88.00, 20, '热销'),
('秘制酱汁烤鳗鱼', 72.00, 15, '热销'),
('香辣小龙虾拼盘', 98.00, 30, '热销'),
('黑椒牛柳意面', 45.00, 25, '热销'),
('黄金脆皮炸鸡桶', 56.00, 40, '热销'),
('炭烤羊排佐薄荷酱', 108.00, 10, '热销');


INSERT INTO menu_item (name, price, stock, category) VALUES
('冷萃气泡咖啡', 28.00, 50, '饮品'),
('杨枝甘露冰沙', 22.00, 40, '饮品'),
('手打柠檬绿茶', 16.00, 60, '饮品'),
('芒果百香果特调', 25.00, 35, '饮品'),
('海盐芝士奶盖乌龙', 30.00, 30, '饮品'),
('鲜椰冰美式', 26.00, 45, '饮品');