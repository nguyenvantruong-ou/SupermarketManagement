package com.ou.repositories;

import com.ou.pojos.Manufacturer;
import com.ou.utils.DatabaseUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ManufacturerRepository {

    public List<Manufacturer> getManufacturers(String kw) throws SQLException {
        List<Manufacturer> manufacturers = new ArrayList<>();
        try (Connection connection = DatabaseUtils.getConnection()) {
            String query = "SELECT * FROM Manufacturer " +
                    "WHERE man_name LIKE CONCAT(\"%\", ? , \"%\")";
            if (kw == null)
                kw = "";
            else kw=kw.trim();
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, kw);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Manufacturer manufacturer = new Manufacturer();
                int manId = resultSet.getInt("man_id");
                manufacturer.setManId(manId);
                manufacturer.setManName(resultSet.getString("man_name"));
                manufacturer.setProductAmount(getProductAmount(manId));
                manufacturer.setManIsActive(resultSet.getBoolean("man_is_active"));
                manufacturers.add(manufacturer);
            }
        }
        return manufacturers;
    }

    // Lấy số lượng sản phẩm của nhà sản xuất
    private int getProductAmount(int manId) throws SQLException {
        if (manId <= 0)
            return 0;
        try (Connection connection = DatabaseUtils.getConnection()) {
            String query = "SELECT COUNT(p.pro_id) as pro_amount " +
                    "FROM Manufacturer m JOIN Product p ON m.man_id = p.man_id " +
                    "WHERE m.man_id = ? AND m.man_is_active = TRUE";
            PreparedStatement prepareStatement = connection.prepareStatement(query);
            prepareStatement.setInt(1, manId);
            ResultSet resultSet = prepareStatement.executeQuery();
            if (resultSet.next())
                return resultSet.getInt("pro_amount");
            return 0;
        }
    }

    // Lấy tổng số lượng nhà sản xuất
    public int getManufacturerAmount() throws SQLException {
        try (Connection connection = DatabaseUtils.getConnection()) {
            String query = "SELECT COUNT(*) as man_amount FROM Manufacturer WHERE man_is_active = TRUE";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            if (resultSet.next())
                return resultSet.getInt("man_amount");
            return 0;
        }
    }

    // Thêm một nhà sản xuất
    public boolean addManufacturer(Manufacturer manufacturer) throws SQLException {
        try (Connection connection = DatabaseUtils.getConnection()) {
            if (!manufacturer.getManIsActive()) {
                String query = "UPDATE Manufacturer SET man_is_active = TRUE WHERE man_id = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setInt(1, manufacturer.getManId());
                return preparedStatement.executeUpdate() == 1;
            }
            String query = "INSERT INTO Manufacturer (man_name) VALUES (?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, manufacturer.getManName().trim());
            return preparedStatement.executeUpdate() == 1;
        }
    }

    // Chính sửa thông tin nhà sản xuất
    public boolean updateManufacturer(Manufacturer manufacturer) throws SQLException {
        try (Connection connection = DatabaseUtils.getConnection()) {
            String query = "UPDATE Manufacturer SET man_name = ? WHERE man_id = ? AND man_is_active = TRUE";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, manufacturer.getManName().trim());
            preparedStatement.setInt(2, manufacturer.getManId());
            return preparedStatement.executeUpdate() == 1;
        }
    }

    // Xóa một nhà sản xuất
    public boolean deleteManufacturer(Manufacturer manufacturer) throws SQLException {
        try (Connection connection = DatabaseUtils.getConnection()) {
            String query = "UPDATE Manufacturer SET man_is_active = FALSE WHERE man_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, manufacturer.getManId());
            return preparedStatement.executeUpdate() == 1;
        }
    }

    // Kiểm tra nhà sản xuất đó đã tồn tài hay chưa
    public boolean isExistManufacturer(Manufacturer manufacturer) throws SQLException {
        try (Connection connection = DatabaseUtils.getConnection()) {
            String query = "SELECT * FROM Manufacturer WHERE man_name = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, manufacturer.getManName().trim());
            return preparedStatement.executeQuery().next();
        }
    }

    // Kiểm tra nhà sản xuất đó đã tồn tài hay chưa
    public boolean isExistManufacturer(Integer manId) throws SQLException {
        try (Connection connection = DatabaseUtils.getConnection()) {
            String query = "SELECT * FROM Manufacturer WHERE man_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, manId);
            return preparedStatement.executeQuery().next();
        }
    }

    // Lấy tất cả các nhà sản xuất còn hoạt động
    public List<Manufacturer> getAllActiveManufacturer() throws SQLException {
        List<Manufacturer> manufacturers = new ArrayList<>();
        try (Connection connection = DatabaseUtils.getConnection()) {
            String query = "SELECT man_name FROM Manufacturer WHERE man_is_active = TRUE";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                Manufacturer manufacturer = new Manufacturer();
                manufacturer.setManName(resultSet.getString("man_name"));
                manufacturers.add(manufacturer);
            }
        }
        return manufacturers;
    }

    // Láy thông tin của manufacturer dựa vào tên nhà sản xuất
    public Manufacturer getManufacturerByName(String manName) throws SQLException{
        try (Connection connection = DatabaseUtils.getConnection()) {
            String query = "SELECT man_id, man_name FROM Manufacturer WHERE man_name = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, manName.trim());
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()){
                Manufacturer manufacturer = new Manufacturer();
                manufacturer.setManId(resultSet.getInt("man_id"));
                manufacturer.setManName(resultSet.getString("man_name"));
                return manufacturer;
            }
        }
        return null;
    }
}
