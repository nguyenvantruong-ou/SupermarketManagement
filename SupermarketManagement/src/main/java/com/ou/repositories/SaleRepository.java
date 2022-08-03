package com.ou.repositories;

import com.ou.pojos.Sale;
import com.ou.pojos.SalePercent;
import com.ou.services.SalePercentService;
import com.ou.utils.DatabaseUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SaleRepository {
    private final static SalePercentService SALE_PERCENT_SERVICE;
    static {
        SALE_PERCENT_SERVICE = new SalePercentService();
    }
    // Lấy thông tin Sale dựa vào id
    public Sale getSaleById(Integer saleId ) throws SQLException{
        try(Connection connection = DatabaseUtils.getConnection()){
            String query = "SELECT sale.sale_id, sper.sper_id " +
                    "FROM Sale sale JOIN SalePercent sper ON sale.sale_id = sper.sper_id " +
                    "WHERE sale.sale_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, saleId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                int sperId = resultSet.getInt("sper_id");
                SalePercent salePercent = SALE_PERCENT_SERVICE.getSalePercentById(sperId);
                Sale sale = new Sale();
                sale.setSalePercent(salePercent);
                sale.setSaleId(resultSet.getInt("sale_id"));
                return sale;
            }
        }
        return null;
    }
    // lấy danh sách mã giảm giá
    public List<Sale> getSales(String kw) throws SQLException {
        List<Sale> sales = new ArrayList<>();
        try(Connection connection = DatabaseUtils.getConnection()){
            String query = "SELECT * FROM Sale s JOIN SalePercent sp ON s.sper_id = sp.sper_id WHERE s.sale_id LIKE CONCAT (\"%\", ? , \"%\")";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, kw);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                Sale sale = new Sale();
                sale.setSaleId(resultSet.getInt("sale_id"));
                sale.setSaleIsActive(resultSet.getBoolean("sale_is_active"));
                SalePercent salePercent = new SalePercent();
                salePercent.setSperId(resultSet.getInt("sper_id"));
                salePercent.setSperIsActive(resultSet.getBoolean("sper_is_active"));
                salePercent.setSperPercent(resultSet.getInt("sper_percent"));
                sale.setSalePercent(salePercent);
                sales.add(sale);
            }
        }
        return sales;
    }

    // thêm một mã giảm giá mới
    public boolean addSale(Sale sale) throws SQLException {
        try (Connection connection = DatabaseUtils.getConnection()){
            if(!sale.getSaleIsActive()){
                String query = "UPDATE Sale SET sale_is_active = TRUE WHERE sale_id = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setInt(1, sale.getSaleId());
                return 1 == preparedStatement.executeUpdate();
            }
            String query = "INSERT INTO Sale(sper_id) VALUES(?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, sale.getSalePercent().getSperId());
            return 1 == preparedStatement.executeUpdate();
        }
    }

    // sửa một mã giảm giá
    public boolean updateSale(Sale sale) throws SQLException {
        try (Connection connection = DatabaseUtils.getConnection()){
            String query = "UPDATE Sale SET sper_id = ? WHERE sale_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, sale.getSalePercent().getSperId());
            preparedStatement.setInt(2, sale.getSaleId());
            return 1 == preparedStatement.executeUpdate();
        }
    }

    // xóa một mã giảm giá
    public boolean deleteSale(Sale sale) throws SQLException {
        try (Connection connection = DatabaseUtils.getConnection()){
            String query = "UPDATE Sale SET sale_is_active = FALSE WHERE sale_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, sale.getSaleId());
            return 1 == preparedStatement.executeUpdate();
        }
    }

    // lấy số lượng mã giảm giá đang còn hoạt động
    public int getSaleAmount() throws SQLException {
        try (Connection connection = DatabaseUtils.getConnection()){
            String query = "SELECT COUNT(sale_id) AS sale_amount FROM Sale s JOIN SalePercent sp ON s.sper_id = sp.sper_id " +
                    "WHERE s.sale_is_active = TRUE and sp.sper_is_active = TRUE;";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next())
                return resultSet.getInt("sale_amount");
        }
        return 0;
    }

    // lấy danh sách mã phần trăm giảm giá còn hoạt động
    public List<String> getSalePercentId() {
        List<String> salePercentIds = new ArrayList<>();
        try (Connection connection = DatabaseUtils.getConnection()){
            String query = "SELECT sper_id FROM SalePercent WHERE sper_is_active = TRUE";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                salePercentIds.add(String.valueOf(resultSet.getInt("sper_id")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return salePercentIds;
    }

    // lấy số phần trăm giảm giá theo mã
    public float getSalePercentBySperId(int sperId){
        try (Connection connection = DatabaseUtils.getConnection()){
            String query = "SELECT sper_percent FROM SalePercent WHERE sper_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, sperId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next())
                return resultSet.getFloat("sper_percent");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
