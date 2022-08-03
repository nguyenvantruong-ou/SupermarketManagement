package com.ou.services;

import com.ou.pojos.Product;
import com.ou.pojos.ProductBranch;
import com.ou.pojos.ProductLimitSale;
import com.ou.pojos.ProductUnit;
import com.ou.repositories.ProductRepository;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

public class ProductService {
    private final static ProductRepository PRODUCT_REPOSITORY;

    static {
        PRODUCT_REPOSITORY = new ProductRepository();
    }

    // Lấy thông tin các sản phẩm dựa vào từ khóa
    public List<Product> getProducts(String kw) throws SQLException {
        return PRODUCT_REPOSITORY.getProducts(kw);
    }

    // Lấy thông tin số lượng sản phẩm
    public int getProductAmount() throws SQLException {
        return PRODUCT_REPOSITORY.getProductAmount();
    }

    // Lấy danh sách đơn vị của product
    public List<ProductUnit> getProductUnits(int proId) throws SQLException {
        return PRODUCT_REPOSITORY.getProductUnits(proId);
    }

    // Lấy các chi nhánh có bán sản phẩm còn hoạt động
    public List<ProductBranch> getProductBranches(int proId) throws SQLException {
        return PRODUCT_REPOSITORY.getProductBranches(proId);
    }

    // Lấy số lượng chi nhánh còn hoạt động đang bán sản phẩm
    public int getProductBranchAmount(int proId) throws SQLException {
        return PRODUCT_REPOSITORY.getProductBranchAmount(proId);
    }

    // Lấy số lượng chi nhánh còn hoạt động đang bán sản phẩm
    public int getProductUnitAmount(int proId) throws SQLException {
        return PRODUCT_REPOSITORY.getProductUnitAmount(proId);
    }

    // Lấy các mã giảm giá của sản phẩm
    public List<ProductLimitSale> getProductLimitSales(int proId) throws SQLException {
        return PRODUCT_REPOSITORY.getProductLimitSales(proId);
    }

    // Kiểm tra sự hợp lệ của sản phẩm
    private boolean isValidProduct(Product product) {
        return !(product == null ||
                product.getProName() == null || product.getProName().trim().isEmpty() ||
                product.getProName().trim().length()>=200||
                product.getCategory() == null || product.getManufacturer() == null ||
                product.getProductBranches() == null || product.getProductUnits() == null);
    }

    // Thêm sản phẩm
    public boolean addProduct(Product product) throws SQLException {
        if (!isValidProduct(product) ||
                product.getCategory() == null || product.getManufacturer() == null ||
                product.getProductUnits().size() == 0 || product.getProductBranches().size() == 0)
            return false;
        if (PRODUCT_REPOSITORY.isExistProduct(product)) {
            Product productAdd = PRODUCT_REPOSITORY.getProducts(product.getProName().trim()).get(0);
            if (!productAdd.getProIsActive()) {
                product.setProId(productAdd.getProId());
                product.setProIsActive(productAdd.getProIsActive());
                return PRODUCT_REPOSITORY.addProduct(product);
            }
            return false;
        }
        return PRODUCT_REPOSITORY.addProduct(product);
    }

    // Chỉnh sửa thông tin sản phẩm
    public boolean updateProduct(Product product) throws SQLException {
        if (!isValidProduct(product) ||
                product.getCategory() == null || product.getManufacturer() == null ||
                product.getProductUnits().size() == 0 || product.getProductBranches().size() == 0)
            return false;
        if (!PRODUCT_REPOSITORY.isExistProduct(product.getProId()))
            return false;
        if (PRODUCT_REPOSITORY.isExistProduct(product)) {
            Product existPro = PRODUCT_REPOSITORY.getProductByName(product.getProName());
            if (!Objects.equals(product.getProId(), existPro.getProId()))
                return false;
        }
        return PRODUCT_REPOSITORY.updateProduct(product);
    }

    // Xóa sản phẩm
    public boolean deleteProduct(Product product) throws SQLException {
        if (product == null || product.getProId() == null)
            return false;
        if (!PRODUCT_REPOSITORY.isExistProduct(product.getProId()))
            return false;
        return PRODUCT_REPOSITORY.deleteProduct(product);
    }

    // Lấy thông tin sản phẩm dựa vào tên sản phẩm
    public Product getProductById(int proId) throws SQLException {
        return PRODUCT_REPOSITORY.getProductById(proId);
    }

    // Lấy thông tin giảm giá của sản phẩm
    public ProductLimitSale getProductLimitSaleOfProduct(int proId, java.util.Date date) throws SQLException {
        return PRODUCT_REPOSITORY.getProductLimitSaleOfProduct(proId, date);
    }
}
