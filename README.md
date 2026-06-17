# 🍜 智慧餐厅点单系统 — 运行指南

> **项目定位**：基于原生 Java EE（Servlet + Thymeleaf + MySQL）的手写 MVC 架构点单系统。无 Spring、无 MyBatis、无 Redis 依赖，纯手工实现连接池、乐观锁防超卖、令牌桶限流等高级特性。
>
> **适用对象**：队友快速部署 + 期末报告素材参考。

---

## 📦 技术栈一览

| 组件 | 版本要求 | 说明 |
|------|----------|------|
| JDK | **11+** | 编译与运行环境（`pom.xml` 中 `maven.compiler.source=11`） |
| MySQL | **8.0+** | 数据库（需支持 `utf8mb4` 字符集） |
| Maven | **3.6+** | 项目构建与依赖管理 |
| Tomcat | **9.0+** | Servlet 容器（需支持 Servlet 4.0） |
| Thymeleaf | 3.0.15 | 服务端模板引擎（渲染动态页面） |
| MySQL Connector/J | 8.0.33 | JDBC 驱动 |

---

## 🚀 快速启动（5 步跑起来）

### 第一步：初始化数据库

用你本机的 MySQL 客户端（命令行或 Navicat 均可）执行项目根目录下的 `init.sql`：

```bash
# 命令行方式（在项目根目录执行）
mysql -u root -p < init.sql
```

执行后会创建 `order_system` 数据库，内含 3 张表（`user`、`menu_item`、`orders`）以及 28 道预置菜品数据。

---

### 第二步：修改数据库连接配置 ⚠️ 必改！

打开 `src/main/java/com/arctic/equipment/util/ConnectionPool.java`，找到 **第 27-29 行**：

```java
private static final String URL = "jdbc:mysql://localhost:3306/order_system?useSSL=false&serverTimezone=UTC&characterEncoding=UTF-8&allowPublicKeyRetrieval=true";
private static final String USER = "root";           // ← 改成你的数据库用户名
private static final String PASSWORD = "Bufan12*";   // ← 改成你的数据库密码
```

> **提示**：如果你的 MySQL 端口不是 `3306`，请在 URL 中一并修改。

---

### 第三步：Maven 编译打包

在项目根目录打开终端，执行：

```bash
mvn clean package
```

编译成功后，`target/` 目录下会生成 `FinalWeb-demo-1.0-SNAPSHOT.war`。

> 首次运行 Maven 会自动下载依赖（`javax.servlet-api`、`mysql-connector-j`、`thymeleaf`），需要几分钟，请保持网络畅通。

---

### 第四步：部署到 Tomcat

1. 将 `target/FinalWeb-demo-1.0-SNAPSHOT.war` 复制到 Tomcat 的 `webapps/` 目录下。
2. 启动 Tomcat：

   ```bash
   # Windows
   cd /d "你的Tomcat安装目录\bin"
   startup.bat

   # macOS / Linux
   cd "你的Tomcat安装目录/bin"
   ./startup.sh
   ```
3. Tomcat 会自动解压 war 包，看到控制台输出 `====== ConnectionPool 初始化完成，池大小: 10 ======` 即表示启动成功。

---

### 第五步：打开浏览器访问

假设 Tomcat 端口为 `8080`，war 包名为 `FinalWeb-demo-1.0-SNAPSHOT.war`：

```
http://localhost:8080/FinalWeb-demo-1.0-SNAPSHOT/
```

系统会自动重定向到登录页。

> **如果你改了 war 包名或配置了不同的上下文路径，请自行替换 URL 中的路径段。**

---

## 🗺️ 关键页面路由表

| 访问路径 | 对应页面/功能 | 说明 |
|----------|---------------|------|
| `/` | 自动跳转登录页 | 根路径重定向 |
| `/login.html` | 登录页 | 公开访问 |
| `/register.html` | 注册页 | 公开访问 |
| `/menu` | 点餐主页 | 需登录（Thymeleaf 动态渲染） |
| `/order/my` | 我的订单 | 需登录 |
| `/order/create` | 下单接口 | POST，需登录 |
| `/order/payCart` | 购物车结算 | POST，需登录 |
| `/logout` | 退出登录 | 清除 Session |
| `/admin/dashboard` | 管理员数据面板 | 需 ADMIN 角色 |
| `/admin/menu/save` | 管理员添加菜品 | POST，需 ADMIN 角色 |
| `/user/profile` | 个人中心 | 需登录 |
| `/user/changePassword` | 修改密码 | POST，需登录 |
| `/user/recharge` | 余额充值 | POST，需登录 |

> **静态页面**（`login.html`、`register.html`、`admin_menu.html`）存放在 `src/main/webapp/` 下，可直接通过 URL 访问。
>
> **动态模板**（`menu.html`、`order_list.html`、`admin_dashboard.html`）存放在 `src/main/webapp/WEB-INF/pages/` 下，**不能直接通过浏览器访问**，必须由 Servlet 转发渲染。

---

## 👤 测试账号

| 类型 | 用户名 | 获取方式 |
|------|--------|----------|
| 普通用户 | 任意 | 访问 `/register.html` 自行注册 |
| 管理员 | `1TEXT1` | 注册该特定用户名，系统自动授予 ADMIN 角色（后端已内置后门） |

---

## 🧪 并发压测工具使用

项目内置了一个并发抢购压测脚本，用于验证**乐观锁防超卖**效果：

**文件位置**：`src/main/java/ConcurrencyStressTest.java`

**使用步骤**：

1. 启动项目，用浏览器登录系统。
2. 按 `F12` 打开开发者工具 → `Application`（或 `Storage`）→ `Cookies`，复制 `JSESSIONID` 的值。
3. 将复制的值填入 `ConcurrencyStressTest.java` 的第 13 行 `COOKIE` 常量中。
4. 运行 `main()` 方法。

**预期结果**：

- 100 个线程同时抢购库存为 1 的"限量帝王蟹"。
- **只有 1 个线程**成功下单。
- 数据库 `menu_item` 表中帝王蟹的 `stock` 字段变为 `0`，**绝不出现负数**。

> 这验证了 `MenuItemDao.decreaseStock()` 中 `WHERE stock >= ?` 的乐观锁机制在高并发下正确工作。

---

## 🏗️ 项目结构速览

```
JavaWebProject-main/
├── init.sql                          # 数据库初始化脚本（建库+建表+种子数据）
├── pom.xml                           # Maven 配置（依赖+编译参数）
├── README.md                         # ← 你正在看的文件
└── src/
    └── main/
        ├── java/
        │   ├── ConcurrencyStressTest.java          # 并发压测工具
        │   └── com/arctic/equipment/
        │       ├── entity/           # 实体层（User, MenuItem, Order, Cart）
        │       ├── dao/              # 数据访问层（UserDao, MenuItemDao, OrderDao）
        │       ├── service/          # 业务逻辑层（OrderService, UserService）
        │       ├── servlet/          # 控制器层（13个 Servlet 路由）
        │       ├── filter/           # 过滤器链（编码/限流/登录鉴权）
        │       ├── listener/         # 生命周期监听（连接池+Thymeleaf 初始化）
        │       └── util/             # 工具层（ConnectionPool 连接池, DBUtil）
        └── webapp/
            ├── login.html            # 登录页（公开）
            ├── register.html         # 注册页（公开）
            ├── admin_menu.html       # 管理员菜品录入页（公开）
            └── WEB-INF/
                └── pages/            # 受保护的 Thymeleaf 动态模板
                    ├── menu.html           # 点餐主页
                    ├── order_list.html     # 我的订单
                    └── admin_dashboard.html # 管理后台数据面板
```

---

## 🔧 常见问题排查

### ❌ 启动时报 "数据库驱动加载失败" 或 "连接池初始化失败"

1. 检查 MySQL 服务是否已启动。
2. 检查 `ConnectionPool.java` 中的 URL、用户名、密码是否正确。
3. 确认 `init.sql` 已成功执行（`order_system` 数据库存在且有 3 张表）。

### ❌ 访问页面中文乱码

1. 确认 `init.sql` 中的建库语句使用了 `utf8mb4`。
2. 确认 `ConnectionPool.java` 的 JDBC URL 中包含 `characterEncoding=UTF-8`。
3. 确认 Tomcat 的 `server.xml` 中 Connector 配置了 `URIEncoding="UTF-8"`。

### ❌ 端口 8080 被占用

1. 找到占用进程：`netstat -ano | findstr 8080`（Windows）/ `lsof -i :8080`（macOS/Linux）。
2. 关闭冲突进程，或修改 Tomcat 的 `server.xml` 中的端口号。

### ❌ Maven 编译报错 "javax.servlet 找不到"

`javax.servlet-api` 的 scope 为 `provided`（Tomcat 自带），本地 IDE 需配置 Tomcat 为运行服务器，或在 `pom.xml` 中临时去掉 `<scope>provided</scope>` 仅用于编译。

### ❌ war 包放入 webapps 后没有自动解压

1. 检查 Tomcat 是否正在运行。
2. 检查 war 包文件名是否包含特殊字符。
3. 手动解压：在 `webapps/` 目录下创建同名文件夹（去掉 `.war` 后缀），将 war 包内容解压进去。

---

## 📝 修改须知（给开发者）

- **表单 `name` 属性不要改动**：Servlet 通过 `request.getParameter("name")` 取值，改了就会收到 `null`。
- **Thymeleaf 的 `th:*` 属性保持原样**：`th:each`、`th:text`、`th:if`、`th:value` 中的 `${...}` 变量名不要改动。
- **隐藏域 `<input type="hidden">` 不要删除**：它承载了菜品 ID 等关键数据。
- **事务边界在 Service 层**：不要尝试在 DAO 或 Servlet 层调用 `commit()` / `rollback()`。

---

## 📄 相关文档

- 项目核心架构与高级特性详解：见期末项目总结报告（队友共享文档）。
- 代码调用链速查：见期末项目总结报告附录部分。

---

> 如有问题，请检查上述排查步骤，或联系项目组其他成员协助排查。