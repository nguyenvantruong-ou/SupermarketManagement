package com.ou.repositories;

import com.ou.pojos.Unit;
import com.ou.utils.DatabaseUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UnitRepository {

    public List<Unit> getUnits(String kw) throws SQLException {
        List<Unit> units = new ArrayList<>();
        try (Connection connection = DatabaseUtils.getConnection()) {
            String query = "SELECT * FROM Unit " +
                    "WHERE uni_name LIKE CONCAT(\"%\", ? , \"%\")";
            if (kw == null)
                kw = "";
            else kw=kw.trim();
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, kw);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Unit unit = new Unit();
                int uniId = resultSet.getInt("uni_id");
                unit.setUniId(uniId);
                unit.setUniName(resultSet.getString("uni_name"));
                unit.setUniIsActive(resultSet.getBoolean("uni_is_active"));
                units.add(unit);
            }
        }
        return units;
    }

    // Lấy tổng số lượng đơn vị
    public int getUnitAmount() throws SQLException {
        try (Connection connection = DatabaseUtils.getConnection()) {
            String query = "SELECT COUNT(*) as uni_amount FROM Unit WHERE uni_is_active = TRUE";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            if (resultSet.next())
                return resultSet.getInt("uni_amount");
            return 0;
        }
    }

    // Thêm một đơn vị
    public boolean addUnit(Unit unit) throws SQLException {
        try (Connection connection = DatabaseUtils.getConnection()) {
            if (!unit.getUniIsActive()) {
                String query = "UPDATE Unit SET uni_is_active = TRUE WHERE uni_id = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setInt(1, unit.getUniId());
                return preparedStatement.executeUpdate() == 1;
            }
            String query = "INSERT INTO Unit (uni_name) VALUES (?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, unit.getUniName().trim());
            return preparedStatement.executeUpdate() == 1;
        }
    }

    // Chính sửa thông tin đơn vị
    public boolean updateUnit(Unit unit) throws SQLException {
        try (Connection connection = DatabaseUtils.getConnection()) {
            String query = "UPDATE Unit SET uni_name = ? WHERE uni_id = ? AND uni_is_active = TRUE";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, unit.getUniName().trim());
            preparedStatement.setInt(2, unit.getUniId());
            return preparedStatement.executeUpdate() == 1;
        }
    }

    // Xóa một đơn vị
    public boolean deleteUnit(Unit unit) throws SQLException {
        try (Connection connection = DatabaseUtils.getConnection()) {
            String query = "UPDATE Unit SET uni_is_active = FALSE WHERE uni_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, unit.getUniId());
            return preparedStatement.executeUpdate() == 1;
        }
    }

    // Kiểm tra đơn vị đó đã tồn tài hay chưa
    public boolean isExistUnit(Unit unit) throws SQLException {
        try (Connection connection = DatabaseUtils.getConnection()) {
            String query = "SELECT * FROM Unit WHERE uni_name = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, unit.getUniName().trim());
            return preparedStatement.executeQuery().next();
        }
    }

    // Kiểm tra đơn vị đó đã tồn tài hay chưa
    public boolean isExistUnit(Integer uniId) throws SQLException {
        try (Connection connection = DatabaseUtils.getConnection()) {
            String query = "SELECT * FROM Unit WHERE uni_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, uniId);
            return preparedStatement.executeQuery().next();
        }
    }


    // Lấy tất cả những đơn vị còn hoạt động
    public List<Unit> getAllActiveUnit() throws SQLException {
        List<Unit> units = new ArrayList<>();
        try (Connection connection = DatabaseUtils.getConnection()) {
            String query = "SELECT uni_name FROM Unit WHERE uni_is_active = TRUE";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                Unit unit = new Unit();
                unit.setUniName(resultSet.getString("uni_name"));
                units.add(unit);
            }
        }
        return units;
    }

    //Lấu thông tin của unit dựa vào tên
    public Unit getUnitByName(String uniName) throws SQLException{
        try (Connection connection = DatabaseUtils.getConnection()) {
            String query = "SELECT uni_id, uni_name FROM Unit WHERE uni_name = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, uniName.trim());
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()){
                Unit unit = new Unit();
                unit.setUniId(resultSet.getInt("uni_id"));
                unit.setUniName(resultSet.getString("uni_name"));
                return unit;
            }
        }
        return null;
    }
}
