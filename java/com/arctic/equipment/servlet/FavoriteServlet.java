package com.arctic.equipment.servlet;

import com.arctic.equipment.dao.FavoriteDao;
import com.arctic.equipment.entity.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import java.io.IOException;

@WebServlet("/favorite")
public class FavoriteServlet extends HttpServlet {

    private FavoriteDao favoriteDao = new FavoriteDao();

    @Override
    protected void doGet(
            HttpServletRequest req,
            HttpServletResponse resp)
            throws ServletException, IOException {

        try {

            Integer itemId =
                    Integer.parseInt(
                            req.getParameter("itemId"));

            HttpSession session =
                    req.getSession();

            User user =
                    (User) session.getAttribute("LOGIN_USER");

            if(user == null){

                resp.sendRedirect(
                        req.getContextPath()
                                + "/login.html"
                );

                return;
            }

            if(favoriteDao.isFavorite(
                    user.getId(),
                    itemId)){

                favoriteDao.removeFavorite(
                        user.getId(),
                        itemId);

            }else{

                favoriteDao.addFavorite(
                        user.getId(),
                        itemId);
            }

            resp.sendRedirect(
                    req.getContextPath()
                            + "/menu"
            );

        } catch (Exception e) {

            e.printStackTrace();

            resp.getWriter().write(
                    "<script>alert('收藏失败');history.back();</script>"
            );
        }
    }
}