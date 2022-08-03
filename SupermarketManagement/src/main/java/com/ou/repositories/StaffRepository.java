package com.ou.repositories;

import com.ou.pojos.Branch;
import com.ou.pojos.Staff;
import com.ou.utils.DatabaseUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StaffRepository {
    //Lấy thông tin của người
    public Staff getStaffById(Integer staId) throws SQLException {
        try(Connection connection = DatabaseUtils.getConnection()){
            String query = "SELECT * FROM Person WHERE pers_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, staId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                Staff staff = new Staff();
                staff.setPersFirstName(resultSet.getString("pers_first_name"));
                staff.setPersLastName(resultSet.getString("pers_last_name"));
                staff.setPersId(resultSet.getInt("pers_id"));
                return staff;
            }
        }
        return null;
    }

    // lấy danh sách staff theo từ khóa
    public List<Staff> getListStaff(String kw) throws SQLException {
        List<Staff> listStaff = new ArrayList<>();
        try (Connection connection = DatabaseUtils.getConnection()) {
            String query = "SELECT * FROM Person,Staff " +
                    "WHERE Person.pers_id = Staff.sta_id AND ( pers_last_name LIKE CONCAT(\"%\", ? , \"%\") OR " +
                    "pers_first_name LIKE CONCAT(\"%\", ? , \"%\") )";
            if (kw == null)
                kw = "";
            else kw=kw.trim();
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, kw);
            preparedStatement.setString(2, kw);

            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                Staff staff = new Staff();
                int id = rs.getInt("pers_id");
                staff.setPersId(id);
                staff.setPersLastName(rs.getString("pers_last_name"));
                staff.setPersFirstName(rs.getString("pers_first_name"));
                staff.setPersIdCard(rs.getString("pers_id_card"));
                staff.setPersPhoneNumber(rs.getString("pers_phone_number"));
                staff.setSex(rs.getByte("pers_sex")==1 ? "Nam" : "Nữ");
                staff.setPersDateOfBirth(rs.getDate("pers_date_of_birth"));
                staff.setPersJoinedDate(rs.getDate("pers_joined_date"));
                staff.setActiveName(rs.getBoolean("pers_is_active")? "Đang hoạt động" : "Ngưng hoạt động");
                staff.setBranchName(getBranchNameById(rs.getInt("bra_id")));
                staff.setRoleName(rs.getBoolean("sta_is_admin") ? "Quản trị viên" : "Nhân viên");
                staff.setStaUsername(rs.getString("sta_username"));
                listStaff.add(staff);
            }
        }
        return listStaff;

    }

    //Lấy tên nhánh từ braId
    public String getBranchNameById(int id) throws SQLException {
        String branchName = "";
        if(id > 0) {
            try (Connection connection = DatabaseUtils.getConnection()) {
                String query = "SELECT bra_name FROM Branch WHERE bra_id = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setInt(1, id);

                ResultSet rs = preparedStatement.executeQuery();

                if (rs.next()){
                    branchName = rs.getString("bra_name");
                    return branchName;
                }
            }
        }
        return branchName;
    }

    // lấy danh sách tên nhánh
    public List<String> getListBranch() throws SQLException {
        List<String> listBranch = new ArrayList<>();
        try (Connection connection = DatabaseUtils.getConnection()) {
            String query = "SELECT bra_name FROM Branch ";
            PreparedStatement preparedStatement = connection.prepareStatement(query);

            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                listBranch.add(rs.getString("bra_name"));
            }
        }
        return listBranch;
    }

    // kiểm tra username tồn tại chưa
    public boolean isUsername(String username) throws SQLException {
        try (Connection connection = DatabaseUtils.getConnection()) {
            String query = "SELECT sta_username FROM Staff WHERE sta_username = ? ";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, username);

            ResultSet rs = preparedStatement.executeQuery();

            if (rs.next()) {
                return  true;
            }
        }
        return false;
    }

    // kiểm tra active của tài khoản
    public boolean isActive(String username) throws SQLException {
        try (Connection connection = DatabaseUtils.getConnection()) {
            String query = "SELECT pers_is_active FROM Person, Staff WHERE Person.pers_id = Staff.sta_id " +
                    " AND sta_username = ? ";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, username);

            ResultSet rs = preparedStatement.executeQuery();

            if (rs.next()) {
                if(!(rs.getBoolean("pers_is_active")))
                    return  false;
                return true;
            }
        }
        return  false;
    }


    // cập nhật active = true
    public boolean setActive(String username) throws SQLException {
        try (Connection connection = DatabaseUtils.getConnection()) {
            String query = "UPDATE Person, Staff SET pers_is_active = TRUE WHERE Person.pers_id = Staff.sta_id " +
                    " AND sta_username = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, username);
            return preparedStatement.executeUpdate() == 1;
        }
    }

    // thêm staff
    public boolean addStaff(Staff staff) throws SQLException {
        try (Connection connection = DatabaseUtils.getConnection()) {
            if(addPerson(staff)){
                String query = "INSERT INTO Staff (sta_id, sta_username, sta_password, sta_is_admin, " +
                        " bra_id) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setInt(1, getPerIdByIdCard(staff.getPersIdCard()));
                preparedStatement.setString(2, staff.getStaUsername());
                preparedStatement.setString(3, staff.getStaPassword());
                preparedStatement.setBoolean(4, staff.getStaIsAdmin());
                preparedStatement.setInt(5, getBraId(staff.getBranchName()));
                return preparedStatement.executeUpdate() == 1;
            }
        }
        return false;
    }

    // thêm dữ liêu vào bảng person
    public boolean addPerson(Staff staff) throws SQLException {
        try (Connection connection = DatabaseUtils.getConnection()) {
            String query = "INSERT INTO Person (pers_id_card, pers_phone_number, pers_sex, " +
                    "pers_last_name, pers_first_name, pers_date_of_birth) " +
                    " VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, staff.getPersIdCard());
            preparedStatement.setString(2, staff.getPersPhoneNumber());
            preparedStatement.setByte(3, staff.getPersSex());
            preparedStatement.setString(4, staff.getPersLastName());
            preparedStatement.setString(5, staff.getPersFirstName());
            java.sql.Date dateOfBirth = new java.sql.Date(staff.getPersDateOfBirth().getTime());
            preparedStatement.setDate(6, dateOfBirth);
            return preparedStatement.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Lấy per_id từ pers_id_card
    public Integer getPerIdByIdCard(String id_card) throws SQLException {
        try (Connection connection = DatabaseUtils.getConnection()) {
            String query = "SELECT pers_id FROM Person WHERE pers_id_card = ? ";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, id_card);

            ResultSet rs = preparedStatement.executeQuery();

            if (rs.next())
                return rs.getInt("pers_id");
        }
        return  0;
    }

    // lấy id nhánh từ tên nhánh
    public Integer getBraId(String bra_name) throws SQLException {
        try (Connection connection = DatabaseUtils.getConnection()) {
            String query = "SELECT bra_id FROM branch WHERE bra_name = ? ";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, bra_name);

            ResultSet rs = preparedStatement.executeQuery();

            if (rs.next())
                return rs.getInt("bra_id");
        }
        return  0;
    }

    // kiểm tra số CMND có trùng không
    public boolean isExistCardId(String card_id) throws SQLException {
        try(Connection connection = DatabaseUtils.getConnection()) {
            String query = "SELECT * FROM Person WHERE pers_id_card = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, card_id);
            return preparedStatement.executeQuery().next();
        }
    }

    // lấy staff theo id
    public Staff getStaffDataById(Integer id) throws SQLException {
        Staff staff = new Staff();
        try (Connection connection = DatabaseUtils.getConnection()) {
            String query = "SELECT pers_id_card, pers_phone_number, pers_sex, pers_last_name, " +
                    "pers_first_name, pers_date_of_birth, sta_username, sta_is_admin, sta_password, pers_is_active, " +
                    "bra_id, pers_joined_date FROM Person, Staff WHERE Person.pers_id = Staff.sta_id AND " +
                    "Staff.sta_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, id);

            ResultSet rs = preparedStatement.executeQuery();

            if (rs.next()) {
                staff.setPersLastName(rs.getString("pers_last_name"));
                staff.setPersFirstName(rs.getString("pers_first_name"));
                staff.setPersIdCard(rs.getString("pers_id_card"));
                staff.setPersPhoneNumber(rs.getString("pers_phone_number"));
                staff.setPersSex(rs.getByte("pers_sex"));
                staff.setPersDateOfBirth(rs.getDate("pers_date_of_birth"));
                staff.setBranchName(getBranchNameById(rs.getInt("bra_id")));
                staff.setStaIsAdmin(rs.getBoolean("sta_is_admin"));
                staff.setStaUsername(rs.getString("sta_username"));
                staff.setStaPassword(rs.getString("sta_password"));
                staff.setPersIsActive(rs.getBoolean("pers_is_active"));
                staff.setPersJoinedDate(rs.getDate("pers_joined_date")); // 13/04/2022
            }
        }
        return staff;
    }

    // cập nhật
    public boolean updateStaff(Staff staff) throws SQLException {
        try (Connection connection = DatabaseUtils.getConnection()) { // update từng thèn 1
            String query = "UPDATE Person, Staff SET  pers_id_card = ?, pers_phone_number = ?, pers_sex = ?, " +
                    " pers_last_name = ?, pers_first_name = ?, pers_date_of_birth = ?, sta_is_admin = ?, bra_id = ? " +
                    " WHERE Person.pers_id = Staff.sta_id AND sta_username = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, staff.getPersIdCard());
            preparedStatement.setString(2, staff.getPersPhoneNumber());
            preparedStatement.setByte(3, staff.getPersSex());
            preparedStatement.setString(4, staff.getPersLastName());
            preparedStatement.setString(5, staff.getPersFirstName());
            java.sql.Date dateOfBirth = new java.sql.Date(staff.getPersDateOfBirth().getTime());
            preparedStatement.setDate(6, dateOfBirth);
            preparedStatement.setBoolean(7, staff.getStaIsAdmin());
            preparedStatement.setInt(8, getBraId(staff.getBranchName()));
            preparedStatement.setString(9, staff.getStaUsername());
            return preparedStatement.executeUpdate() == 2;
        }
    }

    // đặt lại mật khẩu là tên tài khoản
    public boolean resetPassword(String username, String password) throws SQLException {
        try (Connection connection = DatabaseUtils.getConnection()) {
            String query = "UPDATE Staff SET sta_password = ? WHERE  sta_username = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, password);
            preparedStatement.setString(2, username);
            return preparedStatement.executeUpdate() == 1;
        }
    }

    // xóa staff
    public boolean deleteStaff(Integer id) throws SQLException {
        try (Connection connection = DatabaseUtils.getConnection()) {
            String query = "UPDATE Person SET pers_is_active = false WHERE  pers_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, id);
            return preparedStatement.executeUpdate() == 1;
        }
    }

    // Lấy thông tin nhân viên dựa vào username
    public Staff getStaffByUsername(String username) throws SQLException {
        try(Connection connection = DatabaseUtils.getConnection()){
            String query = "SELECT p.pers_id, p.pers_first_name, p.pers_last_name, p.pers_id_card, p.pers_phone_number, " +
                    " p.pers_sex, p.pers_date_of_birth, p.pers_joined_date, b.bra_name, s.sta_username " +
                    "FROM Staff s JOIN Person p ON s.sta_id = p.pers_id " +
                    "JOIN Branch b ON s.bra_id = b.bra_id " +
                    "WHERE s.sta_username = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, username.trim());
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()){
                Staff staff = new Staff();
                Branch branch = new Branch();
                branch.setBraName(resultSet.getString("bra_name"));
                staff.setStaUsername(resultSet.getString("sta_username"));
                staff.setPersId(resultSet.getInt("pers_id"));
                staff.setPersFirstName(resultSet.getString("pers_first_name"));
                staff.setPersLastName(resultSet.getString("pers_last_name"));
                staff.setPersIdCard(resultSet.getString("pers_id_card"));
                staff.setPersPhoneNumber(resultSet.getString("pers_phone_number"));
                staff.setPersSex(resultSet.getByte("pers_sex"));
                staff.setPersDateOfBirth(resultSet.getDate("pers_date_of_birth"));
                staff.setPersJoinedDate(resultSet.getDate("pers_joined_date"));
                staff.setBranch(branch);
                return staff;
            }
        }
        return null;
    }
}
