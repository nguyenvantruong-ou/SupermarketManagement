package com.ou.repositories;

import com.ou.pojos.Unit;
import com.ou.utils.DatabaseUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UnitRepositoryForTest {
    // Lấy thông tin  chi nhánh dựa vào id
    public Unit getUnitById(int uniId) throws SQLException {
        try (Connection connection = DatabaseUtils.getConnection()) {
            String query = "SELECT * FROM Unit " +
                    "WHERE uni_id = ? AND uni_is_active = TRUE";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, uniId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                Unit unit = new Unit();
                unit.setUniId(resultSet.getInt("uni_id"));
                unit.setUniName(resultSet.getString("uni_name"));
                unit.setUniIsActive(resultSet.getBoolean("uni_is_active"));
                return unit;
            }
            return null;
        }
    }

    // Lấy thông tin đơn vị dựa vào tên
    public Unit getUnitByName (String uniName) throws SQLException{
        try(Connection connection = DatabaseUtils.getConnection()){
            String query  = "SELECT * FROM Unit WHERE uni_name = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, uniName);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                Unit unit = new Unit();
                unit.setUniId(resultSet.getInt("uni_id"));
                unit.setUniName(resultSet.getString("uni_name"));
                unit.setUniIsActive(resultSet.getBoolean("uni_is_active"));
                return unit;
            }
            return null;
        }
    }
}
