package com.ou.repositories;

import com.ou.pojos.*;
import com.ou.utils.DatabaseUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductRepositoryForTest {
    // Lấy thông tin sản phẩm dựa vào id
    public Product getProductById(int proId) throws SQLException {
        try(Connection connection = DatabaseUtils.getConnection()){
            String query  = "SELECT * FROM Product " +
                    "WHERE pro_id = ? AND pro_is_active = TRUE";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1,proId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()){
                Product product = new Product();
                product.setProId(resultSet.getInt("pro_id"));
                product.setProName(resultSet.getString("pro_name"));
                product.setProIsActive(resultSet.getBoolean("pro_is_active"));
                return product;
            }
            return null;
        }
    }

    // Lấy thông tin sản phẩm dựa vào tên sản phẩm
    public Product getProductByName(String proName ) throws SQLException{
        try(Connection connection = DatabaseUtils.getConnection()){
            String query = "SELECT pro_id, pro_is_active, pro_name, cat_id, man_id FROM Product WHERE pro_name = ? ";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, proName);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()){
                Product product = new Product();
                Category category = new Category();
                Manufacturer manufacturer = new Manufacturer();
                category.setCatId(resultSet.getInt("cat_id"));
                manufacturer.setManId(resultSet.getInt("man_id"));
                product.setProId(resultSet.getInt("pro_id"));
                product.setProName(resultSet.getString("pro_name"));
                product.setCategory(category);
                product.setManufacturer(manufacturer);
                product.setProIsActive(resultSet.getBoolean("pro_is_active"));
                product.setProductUnits(getProductUnits(product.getProId()));
                product.setProductBranches(getProductBranches(product.getProId()));
                product.setProductLimitSales(getProductLimitSales(product.getProId()));
                return product;
            }
            return null;
        }

    }

    // Lấy danh sách chi nhánh của sản phẩm
    private List<ProductBranch> getProductBranches(int proId) throws SQLException{
        List<ProductBranch> productBranches = new ArrayList<>();
        try(Connection connection = DatabaseUtils.getConnection()){
            String query = "SELECT b.bra_id, b.bra_name, b.bra_address, b.bra_is_active " +
                    "FROM Product_Branch pb JOIN Branch b ON pb.bra_id = b.bra_id " +
                    "WHERE pb.pro_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, proId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                ProductBranch productBranch = new ProductBranch();
                Branch branch = new Branch();
                branch.setBraId(resultSet.getInt("bra_id"));
                branch.setBraName(resultSet.getString("bra_name"));
                branch.setBraAddress(resultSet.getString("bra_address"));
                branch.setBraIsActive(resultSet.getBoolean("bra_is_active"));
                productBranch.setBranch(branch);
                productBranches.add(productBranch);
            }
        }
        return productBranches;
    }

    // Lấy danh sách đơn vị của sản phẩm
    private List<ProductUnit> getProductUnits(int proId) throws SQLException {
        List<ProductUnit> productUnits = new ArrayList<>();
        try (Connection connection = DatabaseUtils.getConnection()) {
            String query = "SELECT pu.pro_price, u.uni_id, u.uni_name, u.uni_is_active " +
                    "FROM Product_Unit pu JOIN Unit u ON pu.uni_id = u.uni_id " +
                    "WHERE pu.pro_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, proId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                ProductUnit  productUnit = new ProductUnit();
                Unit unit = new Unit();
                unit.setUniId(resultSet.getInt("uni_id"));
                unit.setUniName(resultSet.getString("uni_name"));
                unit.setUniIsActive(resultSet.getBoolean("uni_is_active"));
                productUnit.setUnit(unit);
                productUnit.setProPrice(resultSet.getBigDecimal("pro_price"));
                productUnits.add(productUnit);

            }
        }
        return productUnits;
    }

    // Lấy danh sách giảm giá của sản phẩm
    private List<ProductLimitSale> getProductLimitSales(int proId) throws SQLException{
        List<ProductLimitSale> productLimitSales = new ArrayList<>();
        try(Connection connection = DatabaseUtils.getConnection()){
            String query ="SELECT ls.lsal_id, ls.lsal_from_date, ls.lsal_to_date " +
                    "FROM Product_LimitSale pl JOIN LimitSale ls ON pl.lsal_id = ls.lsal_id " +
                    "WHERE pl.pro_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, proId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                ProductLimitSale productLimitSale = new ProductLimitSale();
                LimitSale limitSale = new LimitSale();
                limitSale.setSaleId(resultSet.getInt("lsal_id"));
                limitSale.setLsalFromDate(resultSet.getDate("lsal_from_date"));
                limitSale.setLsalToDate(resultSet.getDate("lsal_to_date"));
                productLimitSale.setLimitSale(limitSale);
                productLimitSales.add(productLimitSale);
            }
        }
        return productLimitSales;
    }
}
