
package com.ou.repositories;

import com.ou.pojos.Category;
import com.ou.utils.DatabaseUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class CategoryRepositoryForTest {
    // Lấy thông tin  danh mục dựa vào id
    public Category getCategoryById(int catId) throws SQLException {
        try (Connection connection = DatabaseUtils.getConnection()) {
            String query = "SELECT * FROM Category " +
                    "WHERE cat_id = ? AND cat_is_active = TRUE";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, catId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                Category category = new Category();
                category.setCatId(resultSet.getInt("cat_id"));
                category.setCatName(resultSet.getString("cat_name"));
                category.setCatIsActive(resultSet.getBoolean("cat_is_active"));
                return category;
            }
            return null;
        }
    }
}