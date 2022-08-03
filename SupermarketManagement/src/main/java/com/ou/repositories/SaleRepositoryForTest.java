package com.ou.repositories;

import com.ou.pojos.Sale;
import com.ou.pojos.SalePercent;
import com.ou.utils.DatabaseUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SaleRepositoryForTest {
    // Lấy thông tin mã giảm giá dựa vào id
    public Sale getSaleById(int saleId) throws SQLException {
        try(Connection connection = DatabaseUtils.getConnection()){
            String query  = "SELECT * FROM Sale s JOIN Salepercent sp ON s.sper_id = sp.sper_id" +
                    " WHERE s.sale_is_active = TRUE and sp.sper_is_active = TRUE and s.sale_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1,saleId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()){
                Sale sale = new Sale();
                sale.setSaleIsActive(resultSet.getBoolean("sale_is_active"));
                sale.setSaleId(resultSet.getInt("sale_id"));
                SalePercent salePercent = new SalePercent();
                salePercent.setSperId(resultSet.getInt("sper_id"));
                salePercent.setSperPercent(resultSet.getInt("sper_percent"));
                salePercent.setSperIsActive(resultSet.getBoolean("sper_is_active"));
                sale.setSalePercent(salePercent);
                return sale;
            }
            return null;
        }
    }
}
