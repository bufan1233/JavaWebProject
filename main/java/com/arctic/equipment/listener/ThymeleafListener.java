package com.arctic.equipment.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import com.arctic.equipment.util.ConnectionPool;

@WebListener
public class ThymeleafListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        // ---- 连接池初始化（必须最先执行，确保后续请求能正常获取连接） ----
        try {
            ConnectionPool.init();
        } catch (Exception e) {
            System.err.println("====== 连接池初始化失败！ ======");
            e.printStackTrace(System.err);
            throw new RuntimeException("连接池初始化失败，应用无法启动", e);
        }

        try {
            // 1. 创建模板解析器 (指定 HTML 文件去哪里找)
            ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(sce.getServletContext());

            // html 文件放在 webapp/WEB-INF/pages/ 目录下
            templateResolver.setPrefix("/WEB-INF/pages/");
            templateResolver.setSuffix(".html");
            templateResolver.setTemplateMode(TemplateMode.HTML);
            templateResolver.setCharacterEncoding("UTF-8");
            // 开发阶段关闭缓存，方便修改 HTML 后立即看到效果
            templateResolver.setCacheable(false);

            // 2. 创建模板引擎并绑定解析器
            TemplateEngine templateEngine = new TemplateEngine();
            templateEngine.setTemplateResolver(templateResolver);

            // 3. 将引擎存入 ServletContext (全局上下文)，供后面的 Servlet 使用
            sce.getServletContext().setAttribute("templateEngine", templateEngine);
            System.out.println("====== Thymeleaf is settled. ======");
        } catch (Exception e) {
            System.err.println("====== Thymeleaf 初始化失败！ ======");
            e.printStackTrace(System.err);
            // 不要抛出异常，避免导致整个应用启动失败
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        ConnectionPool.destroy();
        System.out.println("====== 应用上下文已销毁 ======");
    }
}
