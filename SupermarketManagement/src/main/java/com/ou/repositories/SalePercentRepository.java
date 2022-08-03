package com.ou.repositories;

import com.ou.pojos.SalePercent;
import com.ou.utils.DatabaseUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SalePercentRepository {

    // Lấy danh sách thông tin những mã giảm giá vẫn còn hoạt động dựa vào từ khoá với mã giảm giá
    public List<SalePercent> getSalePercents(int kw) throws SQLException {
        List<SalePercent> salePercents = new ArrayList<>();
        try (Connection connection = DatabaseUtils.getConnection()) {
            String query = "SELECT * FROM SalePercent" ;
            ResultSet resultSet = null;

            if (kw != -1){
                query += " WHERE sper_percent =?";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setInt(1, kw);
                resultSet = preparedStatement.executeQuery();
            } else {
                Statement statement = connection.createStatement();
                resultSet = statement.executeQuery(query);
            }
            while (resultSet.next()) {
                SalePercent salePercent = new SalePercent();
                int sperId = resultSet.getInt("sper_id");
                salePercent.setSperId(sperId);
                salePercent.setSperPercent(resultSet.getInt("sper_percent"));
                salePercent.setSperIsActive(resultSet.getBoolean("sper_is_active"));
                salePercents.add(salePercent);
            }
        }
        return salePercents;
    }

    // Lấy danh sách thông tin những mã giảm giá vẫn còn hoạt động
    public List<SalePercent> getAllActiveSalePercent() throws SQLException {
        List<SalePercent> salePercents = new ArrayList<>();
        try (Connection connection = DatabaseUtils.getConnection()) {
            String query = "SELECT sper_percent FROM SalePercent WHERE sper_is_active = TRUE";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                SalePercent salePercent = new SalePercent();
                salePercent.setSperPercent(resultSet.getInt("sper_percent"));
                salePercents.add(salePercent);
            }
        }
        return salePercents;
    }

    // Lấy thông tin giảm giá dựa vào id
    public SalePercent getSalePercentById(Integer sperId) throws SQLException {
        try(Connection connection = DatabaseUtils.getConnection()){
            String query = "SELECT * FROM SalePercent WHERE sper_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, sperId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                SalePercent salePercent = new SalePercent();
                salePercent.setSperId(resultSet.getInt("sper_id"));
                salePercent.setSperPercent(resultSet.getInt("sper_percent"));
                return salePercent;
            }
        }
        return null;
    }

    // Lấy thông tin của giảm giá dựa vào mã giảm giá
    public SalePercent getSalePercentByPercent(Integer sperPercent) throws SQLException{
        try (Connection connection = DatabaseUtils.getConnection()) {
            String query  = "SELECT sper_id, sper_percent FROM SalePercent WHERE sper_percent = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, sperPercent);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()) {
                SalePercent saleSper = new SalePercent();
                saleSper.setSperId(resultSet.getInt("sper_id"));
                saleSper.setSperPercent(resultSet.getInt("sper_percent"));
                return saleSper;
            }
        }
        return null;
    }

    // Lấy số lượng mã giảm giá
    public int getSalePercentAmount() throws SQLException {
        try (Connection connection = DatabaseUtils.getConnection()) {
            String query = "SELECT COUNT(*) as salePercent_amount FROM SalePercent WHERE sper_is_active = TRUE";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            if (resultSet.next())
                return resultSet.getInt("salePercent_amount");
            return 0;
        }
    }

    // Thêm một mã giảm giá mới
    public boolean addSalePercent(SalePercent salePercent) throws SQLException {

        try (Connection connection = DatabaseUtils.getConnection()) {
            if (!salePercent.getSperIsActive()) {
                String query = "UPDATE SalePercent SET sper_is_active = TRUE WHERE sper_id = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setInt(1, salePercent.getSperId());
                return preparedStatement.executeUpdate() == 1;
            }

            String query = "INSERT INTO SalePercent (sper_percent) VALUES (?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setFloat(1, salePercent.getSperPercent());
            return preparedStatement.executeUpdate() == 1;
        }
    }

    // Chính sửa thông tin của mã giảm giá
    public boolean updateSalePercent(SalePercent salePercent) throws SQLException {
        try (Connection connection = DatabaseUtils.getConnection()) {
            String query = "UPDATE SalePercent SET sper_percent = ? WHERE sper_id = ? AND sper_is_active = TRUE";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setFloat(1, salePercent.getSperPercent());
            preparedStatement.setInt(2, salePercent.getSperId());
            return preparedStatement.executeUpdate() == 1;
        }
    }

    // Xóa một mã giảm giá
    public boolean deleteSalePercent(SalePercent salePercent) throws SQLException {
        try (Connection connection = DatabaseUtils.getConnection()) {
            String query = "UPDATE SalePercent SET sper_is_active = FALSE WHERE sper_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, salePercent.getSperId());
            return preparedStatement.executeUpdate() == 1;
        }
    }

    // Kiểm tra mã giảm giá đã tồn tài hay chưa
    public boolean isExistSalePercent(Integer saleSperId) throws SQLException {
        try (Connection connection = DatabaseUtils.getConnection()) {
            String query = "SELECT * FROM SalePercent WHERE sper_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, saleSperId);
            return preparedStatement.executeQuery().next();
        }
    }

    // Kiểm tra mã giảm giá đã tồn tài hay chưa
    public boolean isExistSalePercent(SalePercent salePercent) throws SQLException {
        try (Connection connection = DatabaseUtils.getConnection()) {
            String query = "SELECT * FROM SalePercent WHERE sper_percent =?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setFloat(1, salePercent.getSperPercent());
            return preparedStatement.executeQuery().next();
        }
    }

    // kiểm tra active của mã giảm giá
    public boolean isActive(Integer saleSperId) throws SQLException {
        try (Connection connection = DatabaseUtils.getConnection()) {
            String query = "SELECT sper_is_active FROM SalePercent WHERE sper_id = ? ";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, saleSperId);

            ResultSet rs = preparedStatement.executeQuery();

            if (rs.next()) {
                return rs.getBoolean("sper_is_active");
            }
        }
        return  false;
    }
}