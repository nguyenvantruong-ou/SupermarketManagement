
package com.ou.services;

import com.ou.pojos.Category;
import com.ou.repositories.CategoryRepositoryForTest;

import java.sql.SQLException;

public class CategoryServiceForTest {
    private final static CategoryRepositoryForTest CATEGORY_REPOSITORY_FOR_TEST;

    static {
        CATEGORY_REPOSITORY_FOR_TEST = new CategoryRepositoryForTest();
    }

    // Lấy thông tin danh mục dựa vào id
    public Category getCategoryById(int catId) throws SQLException{
        if(catId <1 )
            return null;
        return CATEGORY_REPOSITORY_FOR_TEST.getCategoryById(catId);
    }
}
