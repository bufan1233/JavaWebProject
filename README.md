# 🍔 校园二手物资/点单系统 - 前端开发指南


> 后端的底层数据库结构、核心交易链路（高并发防超卖）、权限控制（Filter）以及所有的数据接口（Servlet）已全部开发完毕并经过测试。
> 目前系统已经是一个具备完整逻辑闭环的“完全体”。你的任务是在现有 HTML 骨架的基础上，引入 CSS 框架（如 Bootstrap 或 Tailwind），将页面美化。

## 📁 目录结构说明 (重要！)

在 Java Web 项目中，静态页面和动态模板的存放位置有严格的区别，请务必遵守：

* **`src/main/webapp/` (公共资源区)**：
* **存放内容**：纯静态 HTML、所有的 CSS 文件、JS 脚本、图片等。
* **现有页面**：`login.html` (登录)、`register.html` (注册)、`admin_menu.html` (管理员后台静态录入页)。
* **访问方式**：用户可以直接通过 `http://localhost:8080/login.html` 访问。


* **`src/main/webapp/WEB-INF/pages/` (受保护的动态模板区)**：
* **存放内容**：所有需要 Thymeleaf 引擎渲染数据的 `.html` 文件。
* **现有页面**：`menu.html` (前台点餐页)、`order_list.html` (我的订单)、`admin_dashboard.html` (数据监控盘)。
* **访问规则**：**绝对不能**通过浏览器直接访问这里的物理文件！必须由后端的 Servlet 转发过来。例如，访问 `http://localhost:8080/menu`，后端的 `MenuServlet` 查完数据库后，才会把数据塞进 `menu.html` 渲染出来。



## 🔗 页面跳转与路由闭环图解

为了方便你理解用户在系统中的游走路径，请参考以下路由映射（你可以在 HTML 的 `<a>` 标签和 `<form>` 的 `action` 属性中直接使用这些路径）：

* `/` (根路径) -> 自动重定向至 `login.html`
* `login.html` -> 提交表单至 `/login` -> 成功后跳转 `/menu`
* `register.html` -> 提交表单至 `/register` -> 成功后跳转 `login.html`
* `/menu` -> 渲染出 `menu.html` -> 点击下单提交至 `/order/create` -> 成功后跳转 `/order/my`
* `/order/my` -> 渲染出 `order_list.html`
* `/logout` -> 注销 Session 并跳转 `login.html`
* `admin_menu.html` -> 提交表单至 `/admin/menu/save` -> 成功后刷新本页
* `/admin/dashboard` -> 渲染出 `admin_dashboard.html`

## 🎨 你的开发任务与修改建议

目前我提供的 HTML 只有最基础的 `<table>` 和 `<form>` 标签，没有任何样式。你可以放手去改写页面结构，但**请务必注意以下后端的“命脉”，千万不要误删**：

### 1. 表单的 `name` 属性 (绝对不能改)

后端 Servlet 是通过 `request.getParameter("xxx")` 来抓取数据的。如果你修改了 `<input>` 标签的 `name` 属性，后端就会接收到 `null`。

* **例如**：`login.html` 中的 `<input name="username">`，请保持 `name="username"` 不变，但你可以随意添加 `class="form-control"` 来控制样式。

### 2. Thymeleaf 渲染标签 (`th:*`) (保持原样)

在 `WEB-INF/pages/` 下的文件中，带有 `th:` 前缀的属性是后端塞入数据的占位符。

* **列表渲染**：像 `<tr th:each="item : ${menuList}">` 这种循环渲染，你可以把 `<tr>` 换成你用 CSS 写的商品卡片 `<div>`，但 `th:each` 逻辑要保留。
* **数据绑定**：像 `<td th:text="${item.name}">` 这种绑定，确保 `${...}` 里面的变量名别改。
* **条件判断**：像 `<div th:if="${session.LOGIN_USER.role == 'ADMIN'}">` 这种用于区分角色的代码，请确保包裹在你设计的管理员入口按钮外层。

### 3. 隐藏域的传递 (小心保护)

在 `menu.html` 的下单表单里，有一行 `<input type="hidden" name="itemId" th:value="${item.id}">`。这行代码虽然在页面上看不见，但它是告诉后端“用户买了哪个菜”的关键，美化界面时千万别把它删了。

## 💡 Tip

* **测试账号**：你可以自己去注册页面注册账号测试。如果需要测试管理员功能，**请注册用户名为 `1TEXT1` 的账号**（我已在后端埋入后门，该账号自动拥有最高权限）。

