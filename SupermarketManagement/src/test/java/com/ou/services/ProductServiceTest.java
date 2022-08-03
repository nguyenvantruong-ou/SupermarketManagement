package com.ou.services;

import com.ou.pojos.*;
import com.ou.utils.DatabaseUtils;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductServiceTest {
    private static Connection connection;
    private static ProductService productService;
    private static ProductServiceForTest productServiceForTest;
    private static CategoryServiceForTest categoryServiceForTest;
    private static ManufacturerServiceForTest manufacturerServiceForTest;
    private static UnitServiceForTest unitServiceForTest;
    private static BranchServiceForTest branchServiceForTest;

    public ProductServiceTest() {

    }

    private Product generateProduct(Product product) throws SQLException {
        List<ProductBranch> productBranches = new ArrayList<>();
        List<ProductUnit> productUnits = new ArrayList<>();
        List<ProductLimitSale> productLimitSales = new ArrayList<>();
        Category category = categoryServiceForTest.getCategoryById(1);
        Manufacturer manufacturer = manufacturerServiceForTest.getManufacturerById(1);
        ProductBranch productBranch1 = new ProductBranch();
        ProductBranch productBranch2 = new ProductBranch();
        ProductUnit productUnit1 = new ProductUnit();
        ProductUnit productUnit2 = new ProductUnit();
        Branch branch1 = branchServiceForTest.getBranchById(1);
        Branch branch2 = branchServiceForTest.getBranchById(2);
        Unit unit1 = unitServiceForTest.getUnitById(1);
        Unit unit2 = unitServiceForTest.getUnitById(2);
        LimitSale limitSale1 = new LimitSale();
        LimitSale limitSale2 = new LimitSale();
        ProductLimitSale productLimitSale1 = new ProductLimitSale();
        ProductLimitSale productLimitSale2 = new ProductLimitSale();
        productBranch1.setBranch(branch1);
        productBranch2.setBranch(branch2);
        productUnit1.setUnit(unit1);
        productUnit1.setProPrice(BigDecimal.valueOf(100000));
        productUnit2.setUnit(unit2);
        productUnit2.setProPrice(BigDecimal.valueOf(200000));
        productBranches.add(productBranch1);
        productBranches.add(productBranch2);
        productUnits.add(productUnit1);
        productUnits.add(productUnit2);
        limitSale1.setSaleId(5);
        limitSale2.setSaleId(8);
        productLimitSale1.setLimitSale(limitSale1);
        productLimitSale2.setLimitSale(limitSale2);
        productLimitSales.add(productLimitSale1);
        productLimitSales.add(productLimitSale2);
        product.setProductBranches(productBranches);
        product.setProductUnits(productUnits);
        product.setProductLimitSales(productLimitSales);
        product.setCategory(category);
        product.setManufacturer(manufacturer);
        product.setProName("Tên sản phẩm thứ 11");
        return product;
    }
    
    
    @BeforeAll
    public static void setUpClass() {
        try {
            connection = DatabaseUtils.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        productService = new ProductService();
        productServiceForTest = new ProductServiceForTest();
        categoryServiceForTest = new CategoryServiceForTest();
        manufacturerServiceForTest = new ManufacturerServiceForTest();
        unitServiceForTest = new UnitServiceForTest();
        branchServiceForTest = new BranchServiceForTest();

    }

    @AfterAll
    public static void tearDownClass() {
        if (connection != null)
            try {
                connection.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
    }

    @BeforeEach
    public void setUp() {
    }

    @AfterEach
    public void tearDown() {
    }

    // Kiểm tra lấy thông tin sản phẩm khi từ khóa truyền vào là null
    // Phải trả về tất cả các sản phẩm còn hoạt động (10 nhà sản xuất)
    @Test
    public void testSelectAllProductByNullKw() {
        try {
            List<Product> products = productService.getProducts(null);
            int amount = productService.getProductAmount();
            Assertions.assertEquals(amount, products.size());
            for(int i= 0; i<10; i++)
                Assertions.assertEquals(i+1, products.get(i).getProId());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Kiểm tra lấy thông tin sản phẩm khi từ khóa truyền vào là 1 chuỗi rỗng
    // Phải trả về tất cả các sản phẩm còn hoạt động (10 nhà sản xuất)
    @Test
    public void testSelectAllProductByEmptyKw() {
        try {
            List<Product> products = productService.getProducts("");
            int amount = productService.getProductAmount();
            Assertions.assertEquals(amount, products.size());
            for(int i= 0; i<10; i++)
                Assertions.assertEquals(i+1, products.get(i).getProId());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Kiểm tra lấy thông tin sản phẩm khi từ khóa truyền vào là 1 chuỗi rỗng
    // Phải trả về tất cả các sản phẩm còn hoạt động (10 nhà sản xuất)
    @Test
    public void testSelectAllProductBySpacesKw() {
        try {
            List<Product> products = productService.getProducts("        ");
            int amount = productService.getProductAmount();
            Assertions.assertEquals(amount, products.size());
            for(int i= 0; i<10; i++)
                Assertions.assertEquals(i+1, products.get(i).getProId());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Kiểm tra lấy thông tin sản phẩm khi từ khóa truyền vào là tên 1 sản phẩm dưới database
    // Có 2 nhà sản xuất tên "Tên sản phẩm thứ 1","Tên sản phẩm thứ 10"
    @Test
    public void testSelectAllProductByValidKw() {
        try {
            List<Product> products = productService.getProducts("Tên sản phẩm thứ 1");
            Assertions.assertEquals(2, products.size());
            Assertions.assertEquals(1, products.get(0).getProId());
            Assertions.assertEquals(10, products.get(1).getProId());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Kiểm tra lấy thông tin sản phẩm khi truyền vào là 1 sản phẩm không tồn tại dưới database
    // Không có sản phẩm tên "Tên sản phẩm thứ 99999999999999999"
    @Test
    public void testSelectAllProductByInValid() {
        try {
            List<Product> products = productService.getProducts("Tên sản phẩm thứ 99999999999999999");
            Assertions.assertEquals(0,products.size());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Kiểm tra số lấy số lượng sản phẩm còn hoạt động dưới database
    // Có 10 nhà sản xuất dưới database nhưng chỉ có 9 sản phẩm còn hoạt động
    @Test
    public void testGetProductAmount() {
        try {
            Product product = productServiceForTest.getProductById(1);
            productService.deleteProduct(product);
            int amount = productService.getProductAmount();
            Assertions.assertEquals(9, amount);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Lấy thông tin số lượng đơn vị của sản phẩm có id không tồn tại
    // sản phẩm có id là  9999 có 0 loại đơn vị
    @Test
    public void testGetProductUnitsWithInValidId() {
        try {
            List<ProductUnit> productUnits = productService.getProductUnits(9999);
            Assertions.assertEquals(0, productUnits.size());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Lấy thông tin số lượng đơn vị của sản phẩm có id hợp lệ
    // sản phẩm có id là 1 có 3 loại đơn vị nhưng đơn vị 1 có active = false nên còn 2 loại đơn vị
    @Test
    public void testGetProductUnitsWithValidId() {
        try {
            List<ProductUnit> productUnits = productService.getProductUnits(1);
            Assertions.assertEquals(2, productUnits.size());
            Assertions.assertEquals(2, productUnits.get(0).getPruId());
            Assertions.assertEquals(3, productUnits.get(1).getPruId());

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Lấy thông tin số lượng chi nhánh đang bán sản phẩm có id không tồn tại
    // sản phẩm có id là  9999 có 0 chi nhánh đang bán
    @Test
    public void testGetProductBranchesWithInValidId() {
        try {
            List<ProductBranch> productBranches = productService.getProductBranches(9999);
            Assertions.assertEquals(0, productBranches.size());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Lấy thông tin số lượng chi nhánh đang bán sản phẩm có id hợp lệ
    // sản phẩm có id là 1 có 2 chi nhánh
    @Test
    public void testGetProductBranchesWithValidId() {
        try {
            List<ProductBranch> productBranches = productService.getProductBranches(1);
            Assertions.assertEquals(2, productBranches.size());
            Assertions.assertEquals("Tên chi nhánh thứ 1", productBranches.get(0).getBranch().getBraName());
            Assertions.assertEquals("Tên chi nhánh thứ 2", productBranches.get(1).getBranch().getBraName());

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Lấy thông tin số lượng chi nhánh đang bán sản phẩm có id không tồn tại
    // sản phẩm có id là  9999 có 0 chi nhánh đang bán
    @Test
    public void testGetProductBranchesAmountWithInValidId() {
        try {
            int braAmount = productService.getProductBranchAmount(9999);
            Assertions.assertEquals(0, braAmount);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Lấy thông tin số lượng chi nhánh đang bán sản phẩm có id hợp lệ
    // sản phẩm có id là 1 có 2 chi nhánh
    @Test
    public void testGetProductBranchesAmountWithValidId() {
        try {
            int braAmount = productService.getProductBranchAmount(1);
            Assertions.assertEquals(2, braAmount);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Lấy thông tin số lượng đơn vị của sản phẩm có id không tồn tại
    // sản phẩm có id là  9999 có 0 đơn vị
    @Test
    public void testGetProductUnitsAmountWithInValidId() {
        try {
            int uniAmount = productService.getProductUnitAmount(9999);
            Assertions.assertEquals(0, uniAmount);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Lấy thông tin số lượng đơn vị của sản phẩm có id hợp lệ
    // sản phẩm có id là 1 có 3 đơn vị
    @Test
    public void testGetProductUnitsAmountWithValidId() {
        try {
            int uniAmount = productService.getProductUnitAmount(1);
            Assertions.assertEquals(3, uniAmount);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // Kiểm tra thêm giá trị null khi thêm sản phẩm
    // Trả về false
    @Test
    public void testAddProductWithNull() {
        try {
            Assertions.assertFalse(productService.addProduct(null));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Kiểm tra thêm thông tin sản phẩm khi thông tin không hợp lệ
    //  Trả về false
    @Test
    public void testAddProductWithInvalidInformation() {
        try {
            Product product = new Product();
            product.setProName("");
            product.setProductUnits(null);
            product.setProductBranches(null);
            product.setManufacturer(null);
            product.setCategory(null);
            Assertions.assertFalse(productService.addProduct(product));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Kiểm tra thêm thông tin sản phẩm đã tồn tại
    // Sản phẩm có mã là 2 đã tồn tại. Trả về false
    @Test
    public void testAddProductWithExist() {
        try {
            Product product = productServiceForTest.getProductById(2);
            Assertions.assertFalse(productService.addProduct(product));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Kiểm tra thêm sản phẩm mới thành công
    // Trả về true
    @Test
    public void testAddProductWithValidInformation() {
        try {
            Product product = new Product();
            product = generateProduct(product);
            int preAmo = productService.getProductAmount();
            Assertions.assertTrue(productService.addProduct(product));
            int nextAmo = productService.getProductAmount();
            Assertions.assertNotEquals(preAmo, nextAmo);

            Product productTest = productServiceForTest.getProductByName(product.getProName());
            Assertions.assertEquals(product.getProName(), productTest.getProName());
            Assertions.assertEquals(product.getCategory().getCatId(), productTest.getCategory().getCatId());
            Assertions.assertEquals(product.getManufacturer().getManId(), productTest.getManufacturer().getManId());
            for(int i =0 ; i< product.getProductUnits().size(); i++)
                Assertions.assertEquals(product.getProductUnits().get(i).getUnit().getUniId(),
                        productTest.getProductUnits().get(i).getUnit().getUniId());

            for(int i=0; i< product.getProductBranches().size(); i++)
                Assertions.assertEquals(product.getProductBranches().get(i).getBranch().getBraId(),
                        productTest.getProductBranches().get(i).getBranch().getBraId());

            for(int i=0; i<product.getProductLimitSales().size(); i++)
                Assertions.assertEquals(product.getProductLimitSales().get(i).getLimitSale().getSaleId(),
                        productTest.getProductLimitSales().get(i).getLimitSale().getSaleId());

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // Kiểm tra thêm giá trị null khi sửa sản phẩm
    // Trả về false
    @Test
    public void testUpdateProductWithNull() {
        try {
            Assertions.assertFalse(productService.updateProduct(null));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Kiểm tra sửa thông tin sản phẩm khi thông tin không hợp lệ
    //  Trả về false
    @Test
    public void testUpdateProductWithInvalidInformation() {
        try {
            Product product = productServiceForTest.getProductById(1);
            product.setProName("");
            product.setProductUnits(null);
            product.setProductBranches(null);
            product.setManufacturer(null);
            product.setCategory(null);
            Assertions.assertFalse(productService.updateProduct(product));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Kiểm tra sửa thông tin sản phẩm đã tồn tại mà thông tin vừa sửa
    // trùng với thông tin sản phẩm khác đã tồn tại
    // Sửa thông tin sản phẩm 1 trùng với thông tin sản phẩm 2 mà
    // sản phẩm có mã là 2 đã tồn tại. Trả về false
    @Test
    public void testUpdateProductWithExist() {
        try {
            Product product = productServiceForTest.getProductById(1);
            product.setProName("Tên sản phẩm thứ 2");
            Assertions.assertFalse(productService.updateProduct(product));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Kiểm tra sửa sản phẩm mới thành công
    // Trả về true
    @Test
    public void testUpdateProductWithValidInformation() {
        try {
            Product product = productServiceForTest.getProductById(1);
            product = generateProduct(product);
            Assertions.assertTrue(productService.updateProduct(product));

            Product productTest = productServiceForTest.getProductByName(product.getProName());
            Assertions.assertEquals(product.getProName(), productTest.getProName());
            Assertions.assertEquals(product.getCategory().getCatId(), productTest.getCategory().getCatId());
            Assertions.assertEquals(product.getManufacturer().getManId(), productTest.getManufacturer().getManId());
            for(int i =0 ; i< product.getProductUnits().size(); i++)
                Assertions.assertEquals(product.getProductUnits().get(i).getUnit().getUniId(),
                        productTest.getProductUnits().get(i).getUnit().getUniId());

            for(int i=0; i< product.getProductBranches().size(); i++)
                Assertions.assertEquals(product.getProductBranches().get(i).getBranch().getBraId(),
                        productTest.getProductBranches().get(i).getBranch().getBraId());

            for(int i=0; i<product.getProductLimitSales().size(); i++)
                Assertions.assertEquals(product.getProductLimitSales().get(i).getLimitSale().getSaleId(),
                        productTest.getProductLimitSales().get(i).getLimitSale().getSaleId());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Kiểm tra giá trị null khi xóa sản phẩm
    // Trả về false
    @Test
    public void testDeleteProductWithNull() {
        try {
            Assertions.assertFalse(productService.deleteProduct(null));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Kiểm tra xóa thông tin sản phẩm khi thông tin không hợp lệ
    //  Trả về false
    @Test
    public void testDeleteProductWithInvalidInformation() {
        try {
            Product product = productServiceForTest.getProductById(1);
            product.setProId(null);
            Assertions.assertFalse(productService.deleteProduct(product));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Kiểm tra xóa thông tin sản phẩm không tồn tại
    // sản phẩm có mã là 9999 không tồn tại. Trả về false
    @Test
    public void testDeleteProductrWithExist() {
        try {
            Product product = productServiceForTest.getProductById(1);
            product.setProId(9999999);
            Assertions.assertFalse(productService.deleteProduct(product));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Kiểm tra xóa sản phẩm thành công
    // Trả về true
    @Test
    public void testDeleteManufacturerWithValidInformation() {
        try {
            Product product = productServiceForTest.getProductById(1);
            int preAmo = productService.getProductAmount();
            Assertions.assertTrue(productService.deleteProduct(product));
            int nextAmo = productService.getProductAmount();
            Assertions.assertNotEquals(preAmo, nextAmo);
            Product productTest = productServiceForTest.getProductByName(product.getProName());
            Assertions.assertFalse(productTest.getProIsActive());

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
