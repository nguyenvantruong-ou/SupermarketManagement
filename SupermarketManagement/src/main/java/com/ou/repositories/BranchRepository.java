package com.ou.repositories;

import com.ou.pojos.Branch;
import com.ou.utils.DatabaseUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BranchRepository {

    // Lấy dánh sách thông tin những chi nhánh vẫn còn hoạt động dựa vào từ khóa với tên chi nhánh
    public List<Branch> getBranches(String kw) throws SQLException {
        List<Branch> branches = new ArrayList<>();
        try (Connection connection = DatabaseUtils.getConnection()) {
            String query = "SELECT * FROM Branch " +
                    "WHERE bra_name LIKE CONCAT(\"%\", ? , \"%\")";
            if (kw == null)
                kw = "";
            else kw=kw.trim();
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, kw);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Branch branch = new Branch();
                int braId = resultSet.getInt("bra_id");
                branch.setBraId(braId);
                branch.setBraName(resultSet.getString("bra_name"));
                branch.setBraAddress(resultSet.getString("bra_address"));
                branch.setProductAmount(getProductAmount(braId));
                branch.setStaffAmount(getStaffAmount(braId));
                branch.setBraIsActive(resultSet.getBoolean("bra_is_active"));
                branches.add(branch);
            }
        }
        return branches;
    }

    // Lấy số lượng sản phẩm của chi nhánh
    private int getProductAmount(int braId) throws SQLException {
        if (braId <= 0)
            return 0;
        try (Connection connection = DatabaseUtils.getConnection()) {
            String query = "SELECT COUNT(pb.pro_id) as pro_amount " +
                    "FROM Branch b JOIN Product_Branch pb ON b.bra_id = pb.bra_id " +
                    "WHERE b.bra_id = ? AND b.bra_is_active = TRUE";
            PreparedStatement prepareStatement = connection.prepareStatement(query);
            prepareStatement.setInt(1, braId);
            ResultSet resultSet = prepareStatement.executeQuery();
            if (resultSet.next())
                return resultSet.getInt("pro_amount");
            return 0;
        }
    }

    // Lấy số lượng nhân viên của chi nhánh
    private int getStaffAmount(int braId) throws SQLException {
        if (braId <= 0)
            return 0;
        try (Connection connection = DatabaseUtils.getConnection()) {
            String query = "SELECT COUNT(s.sta_id) as sta_amount " +
                    "FROM Branch b JOIN Staff s ON b.bra_id = s.bra_id " +
                    "WHERE b.bra_id = ? AND b.bra_is_active = TRUE";
            PreparedStatement prepareStatement = connection.prepareStatement(query);
            prepareStatement.setInt(1, braId);
            ResultSet resultSet = prepareStatement.executeQuery();
            if (resultSet.next())
                return resultSet.getInt("sta_amount");
            return 0;
        }
    }

    // Lấy tổng số lượng chi nhánh
    public int getBranchAmount() throws SQLException {
        try (Connection connection = DatabaseUtils.getConnection()) {
            String query = "SELECT COUNT(*) as bra_amount FROM Branch WHERE bra_is_active = TRUE";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            if (resultSet.next())
                return resultSet.getInt("bra_amount");
            return 0;
        }
    }

    // Thêm một chi nhánh mới
    public boolean addBranch(Branch branch) throws SQLException {
        try (Connection connection = DatabaseUtils.getConnection()) {
            if (!branch.getBraIsActive()) {
                String query = "UPDATE Branch SET bra_is_active = TRUE WHERE bra_id = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setInt(1, branch.getBraId());
                return preparedStatement.executeUpdate() == 1;
            }

            String query = "INSERT INTO Branch (bra_name, bra_address) VALUES (?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, branch.getBraName().trim());
            preparedStatement.setString(2, branch.getBraAddress().trim());
            return preparedStatement.executeUpdate() == 1;
        }
    }

    // Chính sửa thông tin của chi nhánh
    public boolean updateBranch(Branch branch) throws SQLException {
        try (Connection connection = DatabaseUtils.getConnection()) {
            String query = "UPDATE Branch SET bra_name = ? , bra_address = ? WHERE bra_id = ? AND bra_is_active = TRUE";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, branch.getBraName().trim());
            preparedStatement.setString(2, branch.getBraAddress().trim());
            preparedStatement.setInt(3, branch.getBraId());
            return preparedStatement.executeUpdate() == 1;
        }
    }

    // Xóa một chi nhánh
    public boolean deleteBranch(Branch branch) throws SQLException {
        try (Connection connection = DatabaseUtils.getConnection()) {
            String query = "UPDATE Branch SET bra_is_active = FALSE WHERE bra_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, branch.getBraId());
            return preparedStatement.executeUpdate() == 1;
        }
    }

    // Kiểm tra chi nhánh đó đã tồn tài hay chưa
    public boolean isExistBranch(Branch branch) throws SQLException {
        try (Connection connection = DatabaseUtils.getConnection()) {
            String query = "SELECT * FROM Branch WHERE bra_name = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, branch.getBraName().trim());
            return preparedStatement.executeQuery().next();
        }
    }

    // Kiểm tra nhà sản xuất đó đã tồn tài hay chưa
    public boolean isExistBranch(Integer braId) throws SQLException {
        try (Connection connection = DatabaseUtils.getConnection()) {
            String query = "SELECT * FROM Branch WHERE bra_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, braId);
            return preparedStatement.executeQuery().next();
        }
    }

    // Lấy thông tin các chi nhánh còn hoạt động
    public List<Branch> getAllActiveBranch() throws SQLException {
        List<Branch> branches = new ArrayList<>();
        try (Connection connection = DatabaseUtils.getConnection()) {
            String query = "SELECT bra_name FROM Branch WHERE bra_is_active = TRUE";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                Branch branch = new Branch();
                branch.setBraName(resultSet.getString("bra_name"));
                branches.add(branch);
            }
        }
        return branches;
    }

    // Lấy thông tin của chi nhánh dựa vào tên chi nhánh
    public Branch getBranchByName(String braName) throws SQLException{
        try (Connection connection = DatabaseUtils.getConnection()) {
            String query  = "SELECT bra_id, bra_name, bra_address FROM Branch WHERE bra_name = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, braName.trim());
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()) {
                Branch branch = new Branch();
                branch.setBraId(resultSet.getInt("bra_id"));
                branch.setBraName(resultSet.getString("bra_name"));
                branch.setBraAddress(resultSet.getString("bra_address"));
                return branch;
            }
        }
        return null;
    }

    // Kiểm tra địa chỉ chi nhánh tồn tại hay chưa
    public boolean isExistAddress(String braAddress) throws SQLException{
        try(Connection connection = DatabaseUtils.getConnection()){
            String query = "SELECT * FROM Branch WHERE bra_address = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, braAddress);
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        }
    }

    // Lấy thông tin  chi nhánh dựa vào id
    public Branch getBranchById(int braId) throws SQLException {
        try (Connection connection = DatabaseUtils.getConnection()) {
            String query = "SELECT * FROM Branch WHERE bra_id = ? ";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, braId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                Branch branch = new Branch();
                branch.setBraId(resultSet.getInt("bra_id"));
                branch.setBraName(resultSet.getString("bra_name"));
                branch.setBraAddress(resultSet.getString("bra_address"));
                branch.setBraIsActive(resultSet.getBoolean("bra_is_active"));
                return branch;
            }
            return null;
        }
    }
}
