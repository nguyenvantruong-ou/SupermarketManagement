
package com.ou.services;

import com.ou.pojos.Category;
import com.ou.repositories.CategoryRepository;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

public class CategoryService {
    private final static CategoryRepository CATEGORY_REPOSITORY;
    static {
        CATEGORY_REPOSITORY = new CategoryRepository();
    }
    //Lấy danh sách danh mục
    public List<Category> getCategories(String kw) throws SQLException{
        return CATEGORY_REPOSITORY.getCategories(kw);
    }
    //lấy tổng số danh mục sản phẩm
    public int getCategoryAmount() throws SQLException{
        return CATEGORY_REPOSITORY.getCategoryAmount();
    }
    //Thêm danh mục
    public boolean addCategory(Category category) throws SQLException{
        if (category == null ||
                category.getCatName() ==  null || 
                category.getCatName().trim().isEmpty())
            return false;
        if (CATEGORY_REPOSITORY.isExistCategory(category)) {
            Category categoryAdd = CATEGORY_REPOSITORY.getCategories(category.getCatName()
                    .trim()).get(0);
            if (!categoryAdd.getCatIsActive())
                return CATEGORY_REPOSITORY.addCategory(categoryAdd);
            return false;
        }
        return CATEGORY_REPOSITORY.addCategory(category);
    }
    //Sửa danh mục
    public boolean updateCategory(Category category) throws SQLException{
        if (category == null ||
                category.getCatId() == null ||
                category.getCatName() ==  null || 
                category.getCatName().trim().isEmpty())
            return false;
        if (CATEGORY_REPOSITORY.isExistCategory(category)){
            Category existCategory = CATEGORY_REPOSITORY.getCategoryByName(category.getCatName());
            if(!Objects.equals(existCategory.getCatId(), category.getCatId()))
                return false;
        }

        return CATEGORY_REPOSITORY.updateCategory(category);
    }
    //Xóa danh mục
    public boolean deleteCategory(Category category) throws SQLException{
        if (category == null ||category.getCatId() == null)
            return false;
        if (!CATEGORY_REPOSITORY.isExistCategory(category.getCatId()))
            return false;
        return CATEGORY_REPOSITORY.deleteCategory(category);
    }
    // Lấy tất cả những category còn hoạt động
    public List<Category> getAllActiveCategory() throws SQLException {
        return CATEGORY_REPOSITORY.getAllActiveCategory();
    }

    // Lấy thông tin của category dựa vào tên loại hàng
    public Category getCategoryByName(String catName) throws SQLException {
        if (catName == null)
            return null;
        return CATEGORY_REPOSITORY.getCategoryByName(catName);
    }
}
