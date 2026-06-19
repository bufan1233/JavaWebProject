package com.arctic.equipment.servlet;
import com.arctic.equipment.entity.User;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/user/profile")
public class UserProfileServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = (User) req.getSession().getAttribute("LOGIN_USER");
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login.html");
            return;
        }
        ServletContext servletContext = getServletContext();
        TemplateEngine engine = (TemplateEngine) servletContext.getAttribute("templateEngine");
        WebContext ctx = new WebContext(req, resp, servletContext, req.getLocale());
        ctx.setVariable("currentUser", user);
        engine.process("user_profile", ctx, resp.getWriter());
    }
}