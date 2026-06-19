package com.arctic.equipment.dao;

import com.arctic.equipment.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;

public class FavoriteDao {

    public void addFavorite(Integer userId,Integer itemId)
            throws SQLException {

        Connection conn = DBUtil.getConnection();

        String checkSql =
                "select * from favorite where user_id=? and item_id=?";

        try(PreparedStatement checkPs =
                    conn.prepareStatement(checkSql)) {

            checkPs.setInt(1,userId);
            checkPs.setInt(2,itemId);

            if(checkPs.executeQuery().next()){

                return;
            }
        }

        String sql =
                "insert into favorite(user_id,item_id) values(?,?)";

        try(PreparedStatement pstmt =
                    conn.prepareStatement(sql)) {

            pstmt.setInt(1,userId);
            pstmt.setInt(2,itemId);

            pstmt.executeUpdate();
        }
    }
    public boolean isFavorite(
            Integer userId,
            Integer itemId)
            throws SQLException {

        Connection conn = DBUtil.getConnection();

        String sql =
                "select * from favorite where user_id=? and item_id=?";

        try(PreparedStatement pstmt =
                    conn.prepareStatement(sql)) {

            pstmt.setInt(1,userId);
            pstmt.setInt(2,itemId);

            return pstmt.executeQuery().next();
        }
    }
    public List<Integer> findFavoriteIds(Integer userId)
            throws SQLException {

        List<Integer> list = new ArrayList<>();

        Connection conn = DBUtil.getConnection();

        String sql =
                "select item_id from favorite where user_id=?";

        try(PreparedStatement pstmt =
                    conn.prepareStatement(sql)) {

            pstmt.setInt(1,userId);

            ResultSet rs = pstmt.executeQuery();

            while(rs.next()) {

                list.add(rs.getInt("item_id"));
            }
        }

        return list;
    }
    public void removeFavorite(
            Integer userId,
            Integer itemId)
            throws SQLException {

        Connection conn = DBUtil.getConnection();

        String sql =
                "delete from favorite where user_id=? and item_id=?";

        try(PreparedStatement pstmt =
                    conn.prepareStatement(sql)) {

            pstmt.setInt(1,userId);
            pstmt.setInt(2,itemId);

            pstmt.executeUpdate();
        }
    }
}
