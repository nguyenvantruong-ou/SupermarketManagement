
package com.ou.repositories;

import com.ou.pojos.Category;
import com.ou.utils.DatabaseUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class CategoryRepository {
    // Lấy dánh sách các danh mục theo từ khóa
    public List<Category> getCategories(String kw) throws SQLException {
        List<Category> cates = new ArrayList<>();
        try ( Connection connection = DatabaseUtils.getConnection()) {
            String query = "SELECT * FROM Category WHERE cat_name LIKE CONCAT(\"%\", ? , \"%\")";
            if (kw == null) {
                kw = "";
            }else
                kw = kw.trim();
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, kw);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Category cate = new Category();
                int catId = resultSet.getInt("cat_id");
                cate.setCatId(catId);
                cate.setCatName(resultSet.getString("cat_name"));
                cate.setProductAmount(getProductAmount(catId));
                cate.setCatIsActive(resultSet.getBoolean("cat_is_active"));
                cates.add(cate);
            }
        }
        return cates;
    }

    // Lấy số lượng sản phẩm của danh mục
    private int getProductAmount(int catId) throws SQLException {
        if (catId <= 0)
            return 0;
        try (Connection connection = DatabaseUtils.getConnection()) {
            String query = "SELECT COUNT(p.pro_id) as pro_amount FROM Product p JOIN Category c ON p.cat_id = c.cat_id " +
                    "WHERE c.cat_id = ? AND c.cat_is_active = TRUE";
            PreparedStatement prepareStatement = connection.prepareStatement(query);
            prepareStatement.setInt(1, catId);
            ResultSet resultSet = prepareStatement.executeQuery();
            if (resultSet.next())
                return resultSet.getInt("pro_amount");
            return 0;
        }
    }

    // Lấy tổng số danh mục sản phẩm
    public int getCategoryAmount() throws SQLException {
        try (Connection connection = DatabaseUtils.getConnection()) {
            String query = "SELECT COUNT(*) as cat_amount FROM Category WHERE cat_is_active = TRUE";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            if (resultSet.next())
                return resultSet.getInt("cat_amount");
            return 0;
        }
    }

    // Thêm một danh mục mới
    public boolean addCategory(Category category) throws SQLException {
        try(Connection connection = DatabaseUtils.getConnection()){
            if (!category.getCatIsActive()) {
                String query = "UPDATE Category SET cat_is_active = TRUE WHERE cat_id = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setInt(1, category.getCatId());
                return preparedStatement.executeUpdate() == 1;
            }
            String query = "INSERT INTO Category (cat_name) VALUES (?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, category.getCatName().trim());
            return preparedStatement.executeUpdate() == 1;
        }
    }

    // Chính sửa thông tin của danh mục
    public boolean updateCategory(Category category) throws SQLException {
        try(Connection connection = DatabaseUtils.getConnection()){
            String query = "UPDATE Category SET cat_name = ? WHERE cat_id = ? and cat_is_active = TRUE";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, category.getCatName().trim());
            preparedStatement.setInt(2, category.getCatId());
            return preparedStatement.executeUpdate() == 1;
        }
    }

    // Xóa một danh mục
    public  boolean deleteCategory (Category category) throws SQLException{
        try(Connection connection = DatabaseUtils.getConnection()){
            String query = "UPDATE Category SET cat_is_active = FALSE WHERE cat_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, category.getCatId());
            return preparedStatement.executeUpdate() == 1;
        }
    }

    // Kiểm tra danh mục đó đã tồn tại hay chưa
    public boolean isExistCategory (Category category) throws SQLException {
        try(Connection connection = DatabaseUtils.getConnection()){
            String query  = "SELECT * FROM Category " +
                    "WHERE cat_name = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, category.getCatName().trim());
            return preparedStatement.executeQuery().next();
        }
    }

    // Kiểm tra danh mục đó đã tồn tại hay chưa
    public boolean isExistCategory (Integer catId) throws SQLException {
        try(Connection connection = DatabaseUtils.getConnection()){
            String query  = "SELECT * FROM Category " +
                    "WHERE cat_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, catId);
            return preparedStatement.executeQuery().next();
        }

    }
    // Lấy tất cả những category còn hoạt động
    public List<Category> getAllActiveCategory() throws SQLException {
        List<Category> categories = new ArrayList<>();
        try (Connection connection = DatabaseUtils.getConnection()) {
            String query = "SELECT cat_name FROM Category WHERE cat_is_active = TRUE";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                Category category = new Category();
                category.setCatName(resultSet.getString("cat_name"));
                categories.add(category);
            }
        }
        return categories;
    }

    // Lấy thông tin của category dựa vào tên loại hàng
    public Category getCategoryByName(String catName) throws SQLException {
        try (Connection connection = DatabaseUtils.getConnection()) {
            String query = "SELECT cat_id, cat_name FROM Category WHERE cat_name = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, catName.trim());
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                Category category = new Category();
                category.setCatId(resultSet.getInt("cat_id"));
                category.setCatName(resultSet.getString("cat_name"));
                return category;
            }
        }
        return null;
    }
}
