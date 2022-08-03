package com.ou.repositories;

import com.ou.pojos.LimitSale;
import com.ou.pojos.SalePercent;
import com.ou.services.SaleService;
import com.ou.utils.DatabaseUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LimitSaleRepository {
    private final static SaleService SALE_SERVICE;
    static {
        SALE_SERVICE = new SaleService();
    }

    // Lấy thông tin limit sale dựa vào id
    public LimitSale getLimitSaleById(Integer lsalId) throws SQLException {
        try(Connection connection = DatabaseUtils.getConnection()){
            String query = "SELECT lsal.lsal_id, lsal.lsal_from_date, lsal.lsal_to_date " +
                    "FROM LimitSale lsal JOIN Sale s ON lsal.lsal_id= s.sale_id " +
                    "JOIN SalePercent sper ON s.sale_id = sper.sper_id WHERE lsal.lsal_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, lsalId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                LimitSale limitSale = new LimitSale();
                limitSale.setSaleId(resultSet.getInt("lsal_id"));
                limitSale.setSale(SALE_SERVICE.getSaleById(resultSet.getInt("lsal_id")));
                limitSale.setLsalFromDate(resultSet.getDate("lsal_from_date"));
                limitSale.setLsalToDate(resultSet.getDate("lsal_to_date"));
                return limitSale;
            }
        }
        return null;
    }

    // Lấy danh sách các loại giảm giá có thời hạn
    public List<LimitSale> getLimitSales() throws SQLException{
        List<LimitSale> limitSales = new ArrayList<>();
        try(Connection connection =DatabaseUtils.getConnection()){
            String query = "SELECT lsal.lsal_id, lsal.lsal_from_date, lsal.lsal_to_date, sper.sper_percent " +
                    "FROM LimitSale lsal JOIN Sale s ON lsal.lsal_id = s.sale_id " +
                    "JOIN SalePercent sper ON s.sper_id = sper.sper_id " +
                    "WHERE s.sale_is_active = TRUE " +
                    "ORDER BY lsal.lsal_to_date ASC";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            while(resultSet.next()){
                LimitSale limitSale = new LimitSale();
                SalePercent salePercent = new SalePercent();
                limitSale.setSaleId(resultSet.getInt("lsal_id"));
                limitSale.setLsalFromDate(resultSet.getDate("lsal_from_date"));
                limitSale.setLsalToDate(resultSet.getDate("lsal_to_date"));
                salePercent.setSperPercent(resultSet.getInt("sper_percent"));
                limitSale.setSalePercent(salePercent);
                limitSales.add(limitSale);
            }
            return limitSales;
        }
    }

    // Lấy danh sách các loại giảm giá có thời hạn theo ngày
    public List<LimitSale> getLimitSales(Date searchDate) throws SQLException {
        List<LimitSale> limitSales = new ArrayList<>();
        try(Connection connection =DatabaseUtils.getConnection()){
            String query = "";
            PreparedStatement preparedStatement;
            if(searchDate != null){
                query = "SELECT ls.lsal_id, ls.lsal_from_date, ls.lsal_to_date, s.sale_is_active, COUNT(ls.lsal_id) as amount_product " +
                        "FROM LimitSale ls JOIN Sale s ON ls.lsal_id = s.sale_id JOIN Product_LimitSale pl ON ls.lsal_id = pl.lsal_id " +
                        "WHERE ? BETWEEN DATE(ls.lsal_from_date) AND DATE(ls.lsal_to_date) GROUP BY ls.lsal_id";
                preparedStatement = connection.prepareStatement(query);
                preparedStatement.setDate(1, searchDate);
            }
            else{
                query = "SELECT ls.lsal_id, ls.lsal_from_date, ls.lsal_to_date, s.sale_is_active, COUNT(ls.lsal_id) as amount_product\n" +
                        "FROM LimitSale ls JOIN Sale s ON ls.lsal_id = s.sale_id JOIN Product_LimitSale pl ON ls.lsal_id = pl.lsal_id\n" +
                        "GROUP BY ls.lsal_id";
               preparedStatement = connection.prepareStatement(query);
            }
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
                LimitSale limitSale = new LimitSale();
                limitSale.setSaleId(resultSet.getInt("lsal_id"));
                limitSale.setLsalFromDate(resultSet.getDate("lsal_from_date"));
                limitSale.setLsalToDate(resultSet.getDate("lsal_to_date"));
                limitSale.setAmountProduct(resultSet.getInt("amount_product"));
                limitSale.setSaleIsActive(resultSet.getBoolean("sale_is_active"));
                limitSales.add(limitSale);
            }
        return limitSales;
        }
    }

    // lấy tổng sô mã giảm giá có thời hạn
    public int getTotalAmountLimitSale() throws SQLException {
       try (Connection connection = DatabaseUtils.getConnection()){
           String query = "SELECT COUNT(lsal_id) as total_amount_lsal\n" +
                   "FROM LimitSale ls JOIN Sale s ON ls.lsal_id = s.sale_id\n" +
                   "WHERE s.sale_is_active = TRUE";
           Statement statement = connection.createStatement();
           ResultSet resultSet =  statement.executeQuery(query);
           if(resultSet.next())
               return resultSet.getInt("total_amount_lsal");
           else
               return 0;
       }
    }

    // lấy danh sách mã sản phẩm
    public List<String> getListSaleId() throws SQLException {
        List<String> saleIds = new ArrayList<>();
        try (Connection connection = DatabaseUtils.getConnection()){
            String query = "SELECT sale_id FROM Sale";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next())
                saleIds.add(resultSet.getString("sale_id"));
        }
        return saleIds;
    }

    // lấy danh sách mã giảm giá
    public List<String> getListProductId() throws SQLException {
        List<String> productIds = new ArrayList<>();
        try (Connection connection = DatabaseUtils.getConnection()){
            String query = "SELECT pro_id FROM Product WHERE pro_is_active = TRUE";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next())
                productIds.add(resultSet.getString("pro_id"));
        }
        return productIds;
    }

    // lấy danh sách id sản phẩm theo mã giảm giá
    public List<String> getProductIdByLsalId(int lsalId) throws SQLException {
        List<String> productIds = new ArrayList<>();
        try (Connection connection = DatabaseUtils.getConnection()){
            String query = "SELECT pro_id FROM Product_LimitSale WHERE lsal_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, lsalId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next())
                productIds.add(resultSet.getString("pro_id"));
        }
        return productIds;
    }

    // lấy danh sách id sản phẩm không thuộc limit sale
    public List<String> getProductIdNotInLimitSaleByLsalId(int lsalId) throws SQLException {
        List<String> productIds = new ArrayList<>();
        try (Connection connection = DatabaseUtils.getConnection()){
            String query = "SELECT pro_id FROM Product " +
                    "WHERE pro_id NOT IN (SELECT pro_id FROM Product_LimitSale WHERE lsal_id = ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, lsalId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next())
                productIds.add(resultSet.getString("pro_id"));
        }
        return productIds;
    }

    // Lấy thông tin limit sale dựa vào id
    public LimitSale getLimitSaleByLsalId(int lsalId) throws SQLException {
        try(Connection connection = DatabaseUtils.getConnection()){
            String query = "SELECT ls.lsal_id, ls.lsal_from_date, ls.lsal_to_date, s.sale_is_active, " +
                    "COUNT(ls.lsal_id) as amount_product FROM LimitSale ls JOIN Sale s ON ls.lsal_id = s.sale_id " +
                    "JOIN Product_LimitSale pl ON ls.lsal_id = pl.lsal_id WHERE ls.lsal_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, lsalId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                LimitSale limitSale = new LimitSale();
                limitSale.setSaleIsActive(resultSet.getBoolean("sale_is_active"));
                limitSale.setAmountProduct(resultSet.getInt("amount_product"));
                limitSale.setSale(SALE_SERVICE.getSaleById(resultSet.getInt("lsal_id")));
                limitSale.setLsalFromDate(resultSet.getDate("lsal_from_date"));
                limitSale.setLsalToDate(resultSet.getDate("lsal_to_date"));
                return limitSale;
            }
        }
        return null;
    }

    // kiểm tra xem lsal_id có tồn tại không
    public boolean isExitsLimitSale(int lsalId) throws SQLException {
        try(Connection connection = DatabaseUtils.getConnection()){
            String query = "SELECT * FROM LimitSale WHERE lsal_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, lsalId);
            return preparedStatement.executeQuery().next();
        }
    }
    // kiểm tra xem product_id có tồn tại không
    public boolean isExitsProductIdLimitSale(LimitSale limitSale, int proId) throws SQLException {
        try(Connection connection = DatabaseUtils.getConnection()){
            String query = "SELECT * FROM Product_LimitSale WHERE lsal_id = ? and pro_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, limitSale.getSaleId());
            preparedStatement.setInt(2, proId);
            return preparedStatement.executeQuery().next();
        }
    }

    // thêm một limit sale
    public boolean addLimitSale(LimitSale limitSale, int proId) throws SQLException {
        try(Connection connection = DatabaseUtils.getConnection()){
            String query = "";
            PreparedStatement preparedStatement;
            if(proId == 0 && !limitSale.getSaleIsActive()){
                query = "UPDATE Sale SET sale_is_active = TRUE WHERE sale_id = ?";
                preparedStatement = connection.prepareStatement(query);
                preparedStatement.setInt(1, limitSale.getSaleId());
                preparedStatement.executeUpdate();
                return 1 == preparedStatement.executeUpdate();
            }
            if(!isExitsLimitSale(limitSale.getSaleId())){
                query = "INSERT INTO LimitSale (lsal_id, lsal_from_date, lsal_to_date) VALUES (?, ?, ?)";
                preparedStatement = connection.prepareStatement(query);
                preparedStatement.setInt(1, limitSale.getSaleId());
                preparedStatement.setDate(2, (Date) limitSale.getLsalFromDate());
                preparedStatement.setDate(3, (Date) limitSale.getLsalToDate());
                preparedStatement.executeUpdate();
            }
            query = "INSERT INTO Product_LimitSale (lsal_id, pro_id) VALUES (?, ?)";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, limitSale.getSaleId());
            preparedStatement.setInt(2, proId);
            return 1 == preparedStatement.executeUpdate();
        }
    }

    // cập nhật thông tin limit sale
    public boolean updateLimitSale(LimitSale limitSale) throws SQLException {
        try(Connection connection = DatabaseUtils.getConnection()){
            String query = "UPDATE LimitSale SET lsal_from_date = ?, lsal_to_date = ? WHERE lsal_id = ? ";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setDate(1, (Date) limitSale.getLsalFromDate());
            preparedStatement.setDate(2, (Date) limitSale.getLsalToDate());
            preparedStatement.setInt(3, limitSale.getSaleId());
            return 1 == preparedStatement.executeUpdate();
        }
    }

    // xóa 1 limit sale
    public boolean deleteLimitSale(LimitSale limitSale, int proId) throws SQLException {
        try(Connection connection = DatabaseUtils.getConnection()){
            PreparedStatement preparedStatement;
            if(proId == 0){
                String query = "UPDATE Sale SET sale_is_active = FALSE WHERE sale_id = ?";
                preparedStatement = connection.prepareStatement(query);
                preparedStatement.setInt(1, limitSale.getSaleId());
            }else {
                String query = "DELETE From Product_LimitSale WHERE lsal_id = ? and pro_id = ?";
                preparedStatement = connection.prepareStatement(query);
                preparedStatement.setInt(1, limitSale.getSaleId());
                preparedStatement.setInt(2, proId);
            }
            return 1 == preparedStatement.executeUpdate();
        }
    }

}
