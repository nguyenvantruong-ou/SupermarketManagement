package com.ou.repositories;

import com.ou.pojos.Staff;
import com.ou.utils.DatabaseUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StaffRepositoryForTest {
    // lấy danh sách các username trong staff
    public List<Staff> getListUsernameStaff() throws SQLException {
        List<Staff> listStaff = new ArrayList<>();
        try (Connection connection = DatabaseUtils.getConnection()) {
            String query = "SELECT sta_username FROM Staff ";
            PreparedStatement preparedStatement = connection.prepareStatement(query);

            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()){
                Staff staff = new Staff();
                staff.setStaUsername(rs.getString("sta_username"));
                listStaff.add(staff);
            }
            return listStaff;
        }
    }

    // lấy mật khẩu theo username
    public String getPasswordStaff(String username) throws SQLException {
        try (Connection connection = DatabaseUtils.getConnection()) {
            String query = "SELECT sta_password FROM Staff WHERE sta_username = ? ";
            PreparedStatement preparedStatement = connection.prepareStatement(query);

            preparedStatement.setString(1, username);

            ResultSet rs = preparedStatement.executeQuery();

            if  (rs.next()){
                return rs.getString("sta_password");
            }
        }
        return "";
    }


    // kiểm tra active của tài khoản theo id
    public boolean isActiveById(Integer id) throws SQLException {
        try (Connection connection = DatabaseUtils.getConnection()) {
            String query = "SELECT pers_is_active FROM Person WHERE sta_id = ? ";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, id);

            ResultSet rs = preparedStatement.executeQuery();

            if (rs.next()) {
                if(!(rs.getBoolean("pers_is_active")))
                    return  false;
                return true;
            }
        }
        return  false;
    }


    // lấy mật khẩu theo username
    public boolean isAdminByUsername(String username) throws SQLException {
        try (Connection connection = DatabaseUtils.getConnection()) {
            String query = "SELECT sta_is_admin FROM Staff WHERE sta_username = ? ";
            PreparedStatement preparedStatement = connection.prepareStatement(query);

            preparedStatement.setString(1, username);

            ResultSet rs = preparedStatement.executeQuery();

            if  (rs.next()){
                return rs.getBoolean("sta_is_admin");
            }
        }
        return false;
    }

}
