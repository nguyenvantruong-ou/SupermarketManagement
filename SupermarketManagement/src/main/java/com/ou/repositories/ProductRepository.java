package com.ou.repositories;

import com.ou.pojos.*;
import com.ou.services.LimitSaleService;
import com.ou.utils.DatabaseUtils;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductRepository {
    private final static LimitSaleService LIMIT_SALE_SERVICE;
    static {
        LIMIT_SALE_SERVICE = new LimitSaleService();
    }
    // Lấy thông tin các sản phẩm
    public List<Product> getProducts(String kw) throws SQLException {
        List<Product> products = new ArrayList<>();
        try (Connection connection = DatabaseUtils.getConnection()) {
            String query = "SELECT p.pro_id, p.pro_name, p.pro_is_active,c.cat_id, c.cat_name, m.man_id, m.man_name "+
                    "FROM Product p JOIN Category c ON p.cat_id = c.cat_id JOIN Manufacturer m ON p.man_id = m.man_id " +
                    "WHERE pro_name LIKE CONCAT(\"%\", ? , \"%\") " +
                    "GROUP BY p.pro_id ,p.pro_name,p.pro_is_active, c.cat_name,m.man_name";
            if (kw == null)
                kw = "";
            else kw=kw.trim();
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, kw);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Product product = new Product();
                int proId = resultSet.getInt("pro_id");
                product.setProId(proId);
                product.setProName(resultSet.getString("pro_name"));
                product.setProIsActive(resultSet.getBoolean("pro_is_active"));
                Category category = new Category();
                category.setCatId(resultSet.getInt("cat_id"));
                category.setCatName(resultSet.getString("cat_name"));
                product.setCategory(category);
                Manufacturer manufacturer = new Manufacturer();
                manufacturer.setManId(resultSet.getInt("man_id"));
                manufacturer.setManName(resultSet.getString("man_name"));
                product.setManufacturer(manufacturer);
                products.add(product);
            }
        }
        return products;
    }

    // Lấy tổng số lượng sản phẩm
    public int getProductAmount() throws SQLException {
        try (Connection connection = DatabaseUtils.getConnection()) {
            String query = "SELECT COUNT(*) as pro_amount FROM Product WHERE pro_is_active = TRUE";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            if (resultSet.next())
                return resultSet.getInt("pro_amount");
            return 0;
        }
    }

    // Lấy danh sách đơn vị của product
    public List<ProductUnit> getProductUnits(int proId) throws SQLException {
        List<ProductUnit> productUnits = new ArrayList<>();
        try (Connection connection = DatabaseUtils.getConnection()) {
            String query = "SELECT pu.pro_price, u.uni_name, pu.pru_id " +
                    "FROM Product_Unit pu JOIN Unit u ON pu.uni_id = u.uni_id JOIN Product p ON pu.pro_id = p.pro_id " +
                    "WHERE p.pro_is_active = TRUE AND pu.pru_is_active =TRUE AND u.uni_is_active = TRUE AND pu.pro_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, proId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                ProductUnit productUnit= new ProductUnit();
                productUnit.setProPrice(BigDecimal.valueOf(resultSet.getFloat("pro_price")));
                productUnit.setPruId(resultSet.getInt("pru_id"));
                Unit unit = new Unit();
                unit.setUniName(resultSet.getString("uni_name"));
                productUnit.setUnit(unit);
                productUnits.add(productUnit);
            }
        }
        return productUnits;
    }

    // Lấy các chi nhánh có bán sản phẩm còn hoạt động
    public List<ProductBranch> getProductBranches (int proId) throws SQLException{
        List<ProductBranch> productBranches = new ArrayList<>();
        try (Connection connection = DatabaseUtils.getConnection()) {
            String query = "SELECT b.bra_name " +
                    "FROM Product_Branch pb JOIN Branch b ON pb.bra_id = b.bra_id JOIN Product p ON pb.pro_id = p.pro_id " +
                    "WHERE p.pro_is_active=TRUE AND b.bra_is_active = TRUE AND pb.pro_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, proId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                ProductBranch productBranch = new ProductBranch();
                Branch branch = new Branch();
                branch.setBraName(resultSet.getString("bra_name"));
                productBranch.setBranch(branch);
                productBranches.add(productBranch);
            }
        }
        return  productBranches;
    }

    // Lấy các mã giảm giá của sản phẩm
    public List<ProductLimitSale> getProductLimitSales(int proId) throws SQLException{
        List<ProductLimitSale> productLimitSales = new ArrayList<>();
        try(Connection connection = DatabaseUtils.getConnection()){
            String query = "SELECT lsal.lsal_from_date, lsal.lsal_id " +
                    "FROM Product_LimitSale pl JOIN Product p ON pl.pro_id = p.pro_id " +
                    "JOIN LimitSale lsal ON pl.lsal_id = lsal.lsal_id " +
                    "WHERE p.pro_is_active = TRUE AND p.pro_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, proId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                ProductLimitSale productLimitSale = new ProductLimitSale();
                LimitSale limitSale = LIMIT_SALE_SERVICE.getLimitSaleById(resultSet.getInt("lsal_id"));
                limitSale.setSaleId(resultSet.getInt("lsal_id"));
                productLimitSale.setLimitSale(limitSale);
                productLimitSales.add(productLimitSale);
            }
            return productLimitSales;
        }
    }

    // Lấy số lượng chi nhánh còn hoạt động đang bán sản phẩm
    public int getProductBranchAmount(int proId) throws SQLException{
        try (Connection connection = DatabaseUtils.getConnection()) {
            String query = "SELECT COUNT(*) as bra_amount " +
                    "FROM Product_Branch pb JOIN Branch b ON pb.bra_id = b.bra_id JOIN Product p ON pb.pro_id = p.pro_id " +
                    "WHERE p.pro_is_active=TRUE AND b.bra_is_active = TRUE AND pb.pro_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, proId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
               return resultSet.getInt("bra_amount");
            }
        }
        return 0;
    }

    // Lấy số lượng đơn vị của sản phẩm
    public int getProductUnitAmount(int proId) throws SQLException{
        try (Connection connection = DatabaseUtils.getConnection()) {
            String query = "SELECT COUNT(*) as uni_amount " +
                    "FROM Product_Unit pu JOIN Unit u ON pu.uni_id = u.uni_id JOIN Product p ON pu.pro_id = p.pro_id " +
                    "WHERE p.pro_is_active = TRUE AND pu.pru_is_active = TRUE " +
                    "AND u.uni_is_active = TRUE AND pu.pro_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, proId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("uni_amount");
            }
        }
        return 0;
    }

    // Xóa những chi nhánh của sản phẩm cũ
    private boolean deleteProductBranch(int proId) throws SQLException {
        try (Connection connection = DatabaseUtils.getConnection()) {
            String query = "DELETE FROM Product_Branch WHERE pro_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, proId);
            return preparedStatement.executeUpdate() == 1;
        }
    }

    // Xóa những đơn giá của sản phẩm cũ
    private boolean deleteProductUnit(int proId) throws SQLException {
        try (Connection connection = DatabaseUtils.getConnection()) {
            String query = "UPDATE Product_Unit SET pru_is_active = FALSE WHERE pro_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, proId);
            return preparedStatement.executeUpdate() == 1;
        }
    }

    // Xóa những giảm giá của sản phẩm
    private boolean deleteProductLimitSale(int proId) throws SQLException {
        try (Connection connection = DatabaseUtils.getConnection()) {
            String query = "DELETE FROM Product_LimitSale WHERE pro_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, proId);
            return preparedStatement.executeUpdate() == 1;
        }
    }

    // Thêm những chi nhánh của sản phẩm mới
    private boolean addProductBranch(int proId, int braId) throws SQLException {
        try (Connection connection = DatabaseUtils.getConnection()) {
            String query = "INSERT INTO Product_Branch (pro_id, bra_id) VALUES (?,?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, proId);
            preparedStatement.setInt(2, braId);
            return preparedStatement.executeUpdate()==1;

        }
    }

    // Thêm những đơn giá của sản phẩm mới
    private boolean addProductUnit(int proId, int uniId, BigDecimal proPrice) throws SQLException {
        try (Connection connection = DatabaseUtils.getConnection()) {
            String query = "INSERT INTO Product_Unit (pro_id, uni_id, pro_price) VALUES (?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, proId);
            preparedStatement.setInt(2, uniId);
            preparedStatement.setDouble(3, Double.parseDouble(proPrice.toString()));
            return preparedStatement.executeUpdate()==1;

        }
    }

    // Thêm giảm giá vào sản phẩm
    private boolean addProductLimitSale(int proId, int lsalId) throws SQLException{
        try(Connection connection = DatabaseUtils.getConnection()){
            String query = "INSERT INTO Product_LimitSale (pro_id, lsal_id) VALUES (?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, proId);
            preparedStatement.setInt(2, lsalId);
            return preparedStatement.executeUpdate() ==1;
        }
    }

    // Cập nhật category của sản phẩm
    private boolean updateProductCategory(int proId, int catId) throws SQLException {
        try (Connection connection = DatabaseUtils.getConnection()) {
            String query = "UPDATE Product SET cat_id = ? WHERE pro_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, catId);
            preparedStatement.setInt(2, proId);
            return preparedStatement.executeUpdate()==1;

        }
    }

    // Cập nhật manufacturer của sản phẩm
    private boolean updateProductManufacturer(int proId, int manId) throws SQLException {
        try (Connection connection = DatabaseUtils.getConnection()) {
            String query = "UPDATE Product SET man_id = ? WHERE pro_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, manId);
            preparedStatement.setInt(2, proId);
            return preparedStatement.executeUpdate()==1;

        }
    }

    // Thêm sản phẩm mới
    public boolean addProduct(Product product) throws SQLException{
        try (Connection connection = DatabaseUtils.getConnection()) {
            if (!product.getProIsActive()) {
                String query = "UPDATE Product SET pro_is_active = TRUE WHERE pro_id = ?;";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setInt(1, product.getProId());
                deleteProductBranch(product.getProId());
                deleteProductUnit(product.getProId());
                deleteProductLimitSale(product.getProId());
                product.getProductBranches().forEach(pb->{
                    try {
                        addProductBranch(product.getProId(), pb.getBranch().getBraId());
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                });
                product.getProductUnits().forEach(pu->{
                    try {
                        addProductUnit(product.getProId(), pu.getUnit().getUniId(), pu.getProPrice());
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                });
                product.getProductLimitSales().forEach(pl->{
                    try {
                        addProductLimitSale(product.getProId(), pl.getLimitSale().getSaleId());
                    }catch (SQLException e){
                        e.printStackTrace();
                    }
                });
                updateProductCategory(product.getProId(), product.getCategory().getCatId());
                updateProductManufacturer(product.getProId(), product.getManufacturer().getManId());
                return preparedStatement.executeUpdate() == 1;
            }
            String query = "INSERT INTO Product (pro_name, cat_id, man_id) VALUES (?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, product.getProName().trim());
            preparedStatement.setInt(2, product.getCategory().getCatId());
            preparedStatement.setInt(3, product.getManufacturer().getManId());
            if ( preparedStatement.executeUpdate() == 1){
                product.setProId( getProductByName(product.getProName()).getProId());
                product.getProductBranches().forEach(pb->{
                    try {
                        addProductBranch(product.getProId(), pb.getBranch().getBraId());
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                });
                product.getProductUnits().forEach(pu->{
                    try {
                        addProductUnit(product.getProId(), pu.getUnit().getUniId(), pu.getProPrice());
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                });
                product.getProductLimitSales().forEach(pl->{
                    try {
                        addProductLimitSale(product.getProId(), pl.getLimitSale().getSaleId());
                    }catch (SQLException e){
                        e.printStackTrace();
                    }
                });
                return true;
            }
            return false;
        }
    }

    // Sửa thông tin sản phẩm
    public boolean updateProduct(Product product) throws SQLException{
        try (Connection connection = DatabaseUtils.getConnection()) {
            String query = "UPDATE Product SET pro_name = ? , cat_id = ?, man_id= ? " +
                    "WHERE pro_id = ? AND pro_is_active = TRUE";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, product.getProName().trim());
            preparedStatement.setInt(2, product.getCategory().getCatId());
            preparedStatement.setInt(3, product.getManufacturer().getManId());
            preparedStatement.setInt(4, product.getProId());
            if(preparedStatement.executeUpdate() == 1){
                deleteProductBranch(product.getProId());
                deleteProductUnit(product.getProId());
                deleteProductLimitSale(product.getProId());
                product.getProductBranches().forEach(pb->{
                    try {
                        addProductBranch(product.getProId(), pb.getBranch().getBraId());
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                });
                product.getProductUnits().forEach(pu->{
                    try {
                        addProductUnit(product.getProId(), pu.getUnit().getUniId(), pu.getProPrice());
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                });
                product.getProductLimitSales().forEach(pl->{
                    try {
                        addProductLimitSale(product.getProId(), pl.getLimitSale().getSaleId());
                    }catch (SQLException e){
                        e.printStackTrace();
                    }
                });
                return true;
            }
            return false;
        }
    }

    // Xóa sản phẩm
    public boolean deleteProduct(Product product) throws SQLException{
        try (Connection connection = DatabaseUtils.getConnection()) {
            String query = "UPDATE Product SET pro_is_active = FALSE WHERE pro_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, product.getProId());
            return preparedStatement.executeUpdate() == 1;
        }
    }

    // Kiểm tra sản phẩm đó đã tồn tài hay chưa
    public boolean isExistProduct(Product product) throws SQLException {
        try (Connection connection = DatabaseUtils.getConnection()) {
            String query = "SELECT * FROM Product WHERE pro_name = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, product.getProName().trim());
            return preparedStatement.executeQuery().next();
        }
    }

    // Kiểm tra sản phẩm đó đã tồn tài hay chưa
    public boolean isExistProduct(int proId) throws SQLException {
        try (Connection connection = DatabaseUtils.getConnection()) {
            String query = "SELECT * FROM Product WHERE pro_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, proId);
            return preparedStatement.executeQuery().next();
        }
    }

    // Lấy thông tin sản phẩm dựa vào tên sản phẩm
    public Product getProductByName(String proName) throws SQLException{
        try (Connection connection = DatabaseUtils.getConnection()) {
            String query = "SELECT pro_id, pro_name FROM Product WHERE pro_name = ?";
            PreparedStatement preparedStatement =connection.prepareStatement(query);
            preparedStatement.setString(1, proName.trim());
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                Product product = new Product();
                product.setProId(resultSet.getInt("pro_id"));
                product.setProName(resultSet.getString("pro_name"));
                return  product;
            }
        }
        return null;
    }

    // Lấy thông tin giảm giá của sản phẩm
    public ProductLimitSale getProductLimitSaleOfProduct(int proId, java.util.Date date) throws SQLException {
        try(Connection connection = DatabaseUtils.getConnection()){
            String query = "SELECT ls.lsal_id " +
                    "FROM Product_LimitSale pl JOIN LimitSale ls ON pl.lsal_id = ls.lsal_id " +
                    "JOIN Sale s ON ls.lsal_id = s.sale_id " +
                    "JOIN SalePercent sper ON s.sper_id = sper.sper_percent " +
                    "WHERE pl.pro_id = ? AND ls.lsal_from_date <= ? AND ls.lsal_to_date >= ? " +
                    "ORDER BY ls.lsal_to_date DESC , sper.sper_percent DESC " +
                    "LIMIT 1";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, proId);
            preparedStatement.setDate(2, (Date) date);
            preparedStatement.setDate(3, (Date) date);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                ProductLimitSale productLimitSale = new ProductLimitSale();
                LimitSale limitSale = LIMIT_SALE_SERVICE.getLimitSaleById(resultSet.getInt("lsal_id"));
                productLimitSale.setLimitSale(limitSale);
                return productLimitSale;
            }
        }
        return null;
    }

    // Lấy thông tin sản phẩm dựa vào mã sản phẩm
    public Product getProductById(int proId) throws SQLException{
        try (Connection connection = DatabaseUtils.getConnection()) {
            String query = "SELECT p.pro_name, c.cat_name, m.man_name " +
                    "FROM Product p JOIN Category c ON p.pro_id = c.cat_id " +
                    "JOIN Manufacturer m ON p.man_id = m.man_id " +
                    "WHERE pro_id = ?";
            PreparedStatement preparedStatement =connection.prepareStatement(query);
            preparedStatement.setInt(1, proId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                Product product = new Product();
                Category category  = new Category();
                Manufacturer manufacturer = new Manufacturer();
                category.setCatName(resultSet.getString("cat_name"));
                manufacturer.setManName(resultSet.getString("man_name"));
                product.setProName(resultSet.getString("pro_name"));
                product.setProId(proId);
                product.setCategory(category);
                product.setManufacturer(manufacturer);
                return  product;
            }
        }
        return null;
    }

}
