package com.ou.repositories;

import com.ou.pojos.SalePercent;
import com.ou.utils.DatabaseUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SalePercentRepositoryForTest {
    // Lấy thông tin mã giảm giá dựa vào id
    public SalePercent getSalePercentById(int sperId) throws SQLException {
        try (Connection connection = DatabaseUtils.getConnection()) {
            String query = "SELECT * FROM SalePercent " +
                    "WHERE sper_id = ? AND sper_is_active = TRUE";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, sperId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                SalePercent salePercent = new SalePercent();
                salePercent.setSperId(resultSet.getInt("sper_id"));
                salePercent.setSperPercent(resultSet.getInt("sper_percent"));
                salePercent.setSperIsActive(resultSet.getBoolean("sper_is_active"));
                return salePercent;
            }
            return null;
        }
    }

    // Lấy thông tin mã giảm giá dựa vào % giảm giá
    public SalePercent getSalePercentByPercent(int sperPercent) throws SQLException {
        try (Connection connection = DatabaseUtils.getConnection()) {
            String query = "SELECT * FROM SalePercent " +
                    "WHERE sper_percent = ? AND sper_is_active = TRUE";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, sperPercent);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                SalePercent salePercent = new SalePercent();
                salePercent.setSperId(resultSet.getInt("sper_id"));
                salePercent.setSperPercent(resultSet.getInt("sper_percent"));
                salePercent.setSperIsActive(resultSet.getBoolean("sper_is_active"));
                return salePercent;
            }
            return null;
        }
    }
}
