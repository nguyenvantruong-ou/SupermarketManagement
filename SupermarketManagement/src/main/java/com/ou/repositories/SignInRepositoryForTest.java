package com.ou.repositories;

import com.ou.utils.DatabaseUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SignInRepositoryForTest {
    // Lấy danh sách username trong bảng staff
    public List<String>  getStaff() throws SQLException {
        List<String> username = new ArrayList<>();
        try (Connection conn = DatabaseUtils.getConnection()) {
            String query = "SELECT * FROM Staff";
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                username.add(rs.getString("sta_username"));
            }
        }
        return username;
    }

    // lấy id theo username
    public Integer getIdbyUsername(String username) throws SQLException {
        try (Connection conn = DatabaseUtils.getConnection()) {
            String query = "SELECT sta_id FROM Staff WHERE sta_username = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            preparedStatement.setString(1, username);
            ResultSet rs = preparedStatement.executeQuery();

            if (rs.next()) {
                return rs.getInt("sta_id");
            }
        }
        return 0;
    }
}
