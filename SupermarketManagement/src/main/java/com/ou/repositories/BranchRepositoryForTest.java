package com.ou.repositories;

import com.ou.pojos.Branch;
import com.ou.utils.DatabaseUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BranchRepositoryForTest {
    // Lấy thông tin  chi nhánh dựa vào id
    public Branch getBranchById(int braId) throws SQLException {
        try (Connection connection = DatabaseUtils.getConnection()) {
            String query = "SELECT * FROM Branch " +
                    "WHERE bra_id = ? AND bra_is_active = TRUE";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, braId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                Branch branch = new Branch();
                branch.setBraId(resultSet.getInt("bra_id"));
                branch.setBraName(resultSet.getString("bra_name"));
                branch.setBraAddress(resultSet.getString("bra_address"));
                branch.setBraIsActive(resultSet.getBoolean("bra_is_active"));
                return branch;
            }
            return null;
        }
    }

    // Lấy thông tin chi nhánh dựa vào tên chi nhánh
    public Branch getBranchByBraName(String braName) throws SQLException {
        try(Connection connection  = DatabaseUtils.getConnection()){
            String query = "SELECT * FROM Branch WHERE bra_name =?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, braName);
            ResultSet resultSet  = preparedStatement.executeQuery();
            if(resultSet.next()){
                Branch branch = new Branch();
                branch.setBraId(resultSet.getInt("bra_id"));
                branch.setBraName(resultSet.getString("bra_name"));
                branch.setBraAddress(resultSet.getString("bra_address"));
                branch.setBraIsActive(resultSet.getBoolean("bra_is_active"));
                return branch;
            }
            return null;
        }
    }
}
