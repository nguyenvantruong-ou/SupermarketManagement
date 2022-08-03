package com.ou.repositories;

import com.ou.pojos.Manufacturer;
import com.ou.utils.DatabaseUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ManufacturerRepositoryForTest {

    // Lấy thông tin  chi nhánh dựa vào id
    public Manufacturer getManufacturerById(int manId) throws SQLException {
        try(Connection connection = DatabaseUtils.getConnection()){
            String query  = "SELECT * FROM Manufacturer " +
                    "WHERE man_id = ? AND man_is_active = TRUE";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1,manId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()){
                Manufacturer manufacturer = new Manufacturer();
                manufacturer.setManId(resultSet.getInt("man_id"));
                manufacturer.setManName(resultSet.getString("man_name"));
                manufacturer.setManIsActive(resultSet.getBoolean("man_is_active"));
                return manufacturer;
            }
            return null;
        }
    }

    // Lấy thông tin nhà sản xuất dựa vào tên nhà sản xuất
    public Manufacturer getManufacturerByName(String manName) throws SQLException{
        try(Connection connection = DatabaseUtils.getConnection()){
            String query = "SELECT * FROM Manufacturer WHERE man_name = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, manName);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                Manufacturer manufacturer = new Manufacturer();
                manufacturer.setManId(resultSet.getInt("man_id"));
                manufacturer.setManName(resultSet.getString("man_name"));
                manufacturer.setManIsActive(resultSet.getBoolean("man_is_active"));
                return manufacturer;
            }
            return null;
        }
    }
}
