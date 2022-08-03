package com.ou.repositories;

import com.ou.pojos.MemberType;
import com.ou.pojos.Sale;
import com.ou.pojos.SalePercent;
import com.ou.utils.DatabaseUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MemberTypeRepositoryForTest {
    // Lấy thông tin  loại thành viên theo id
    public MemberType getMemberTypeById(int memtId) throws SQLException {
        try (Connection connection = DatabaseUtils.getConnection()) {
            String query = "SELECT * FROM MemberType mt JOIN Sale s ON mt.sale_id = s.sale_id" +
                    " and memt_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, memtId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                MemberType memberType = new MemberType();
                memberType.setMemtIsActive(resultSet.getBoolean("memt_is_active"));
                memberType.setMemtId(resultSet.getInt("memt_id"));
                memberType.setMemtName(resultSet.getString("memt_name"));
                memberType.setMemtTotalMoney(resultSet.getBigDecimal("memt_total_money"));
                memberType.setAmountMember(getTotalAmountMember(memberType.getMemtId()));
                Sale sale = new Sale();
                sale.setSaleId(resultSet.getInt("sale_id"));
                sale.setSaleIsActive(resultSet.getBoolean("sale_is_active"));
                sale.setSalePercent(getSalePercentById(resultSet.getInt("sper_id")));
                memberType.setSale(sale);
                return memberType;
            }
            return null;
        }
    }

    public MemberType getMemberTypeByName(String name) throws SQLException {
        try (Connection connection = DatabaseUtils.getConnection()) {
            String query = "SELECT * FROM MemberType mt JOIN Sale s ON mt.sale_id = s.sale_id" +
                    " WHERE memt_is_active = TRUE and memt_name = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, name);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                MemberType memberType = new MemberType();
                memberType.setMemtIsActive(resultSet.getBoolean("memt_is_active"));
                memberType.setMemtId(resultSet.getInt("memt_id"));
                memberType.setMemtName(resultSet.getString("memt_name"));
                memberType.setMemtTotalMoney(resultSet.getBigDecimal("memt_total_money"));
                memberType.setAmountMember(getTotalAmountMember(memberType.getMemtId()));
                Sale sale = new Sale();
                sale.setSaleId(resultSet.getInt("sale_id"));
                sale.setSaleIsActive(resultSet.getBoolean("sale_is_active"));
                sale.setSalePercent(getSalePercentById(resultSet.getInt("sper_id")));
                memberType.setSale(sale);
                return memberType;
            }
            return null;
        }
    }

    // lấy danh sách loại thành viên còn hoạt động
    public List<MemberType> getMemberTypes() throws SQLException {
        List<MemberType> memberTypes = new ArrayList<>();
        try (Connection connection = DatabaseUtils.getConnection()) {
            String query = "SELECT * FROM MemberType mt JOIN Sale s ON mt.sale_id = s.sale_id" +
                    " WHERE memt_is_active = TRUE ORDER BY memt_total_money ASC";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                MemberType memberType = new MemberType();
                memberType.setMemtId(resultSet.getInt("memt_id"));
                memberType.setMemtName(resultSet.getString("memt_name"));
                memberType.setMemtIsActive(resultSet.getBoolean("memt_is_active"));
                memberType.setMemtTotalMoney(resultSet.getBigDecimal("memt_total_money"));
                memberType.setAmountMember(getTotalAmountMember(memberType.getMemtId()));
                Sale sale = new Sale();
                sale.setSaleId(resultSet.getInt("sale_id"));
                sale.setSaleIsActive(resultSet.getBoolean("sale_is_active"));
                sale.setSalePercent(getSalePercentById(resultSet.getInt("sper_id")));
                memberType.setSale(sale);
                memberTypes.add(memberType);
            }
        }
        return memberTypes;
    }

    // lấy tổng số thành viên của loại thành viên theo mã loại thành viên
    public int getTotalAmountMember(int memtId) throws SQLException {
        try (Connection connection = DatabaseUtils.getConnection()) {
            String query = "SELECT COUNT(mt.memt_id) as amount_member " +
                    "FROM MemberType mt JOIN Member m ON mt.memt_id = m.memt_id " +
                    "WHERE mt.memt_id = ? " +
                    "GROUP BY mt.memt_id";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, memtId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next())
                return resultSet.getInt("amount_member");
            else
                return 0;
        }
    }

    // lấy sale percent theo mã
    public SalePercent getSalePercentById(int sperId) {
        SalePercent salePercent = new SalePercent();
        try (Connection connection = DatabaseUtils.getConnection()) {
            String query = "SELECT * FROM SalePercent WHERE sper_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, sperId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                salePercent.setSperId(resultSet.getInt("sper_id"));
                salePercent.setSperIsActive(resultSet.getBoolean("sper_is_active"));
                salePercent.setSperPercent(resultSet.getInt("sper_percent"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return salePercent;
    }
}
