package com.ou.services;

import com.ou.pojos.Manufacturer;
import com.ou.utils.DatabaseUtils;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class ManufacturerServiceTest {
    private static Connection connection;
    private static ManufacturerService manufacturerService;
    private static ManufacturerServiceForTest manufacturerServiceForTest;

    public ManufacturerServiceTest() {

    }

    @BeforeAll
    public static void setUpClass() {
        try {
            connection = DatabaseUtils.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        manufacturerService = new ManufacturerService();
        manufacturerServiceForTest = new ManufacturerServiceForTest();
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

    // Kiểm tra lấy thông tin nhà sản xuất khi từ khóa truyền vào là null
    // Phải trả về tất cả các nhà sản xuất còn liên kết với cửa hàng (4 nhà sản xuất có mã 1,2,3,4)
    @Test
    public void testSelectAllManufacturerByNullKw() {
        try {
            List<Manufacturer> manufacturers = manufacturerService.getManufacturers(null);
            int amount = manufacturerService.getManufacturerAmount();
            Assertions.assertEquals(amount, manufacturers.size());
            Assertions.assertEquals(1, manufacturers.get(0).getManId());
            Assertions.assertEquals(2, manufacturers.get(1).getManId());
            Assertions.assertEquals(3, manufacturers.get(2).getManId());
            Assertions.assertEquals(4, manufacturers.get(3).getManId());

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Kiểm tra lấy thông tin nhà sản xuất khi từ khóa truyền vào là 1 chuỗi rỗng
    // Phải trả về tất cả các nhà sản xuất còn liên kết với cửa hàng (4 nhà sản xuất)
    @Test
    public void testSelectAllManufacturerByEmptyKw() {
        try {
            List<Manufacturer> manufacturers = manufacturerService.getManufacturers("");
            int amount = manufacturerService.getManufacturerAmount();
            Assertions.assertEquals(amount, manufacturers.size());
            Assertions.assertEquals(1, manufacturers.get(0).getManId());
            Assertions.assertEquals(2, manufacturers.get(1).getManId());
            Assertions.assertEquals(3, manufacturers.get(2).getManId());
            Assertions.assertEquals(4, manufacturers.get(3).getManId());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // Kiểm tra lấy thông tin nhà sản xuất khi từ khóa truyền vào là 1 chuỗi khoảng trắng
    // Phải trả về 0
    @Test
    public void testSelectAllManufacturerBySpacesKw() {
        try {
            List<Manufacturer> manufacturers = manufacturerService.getManufacturers("         ");
            int amount = manufacturerService.getManufacturerAmount();
            Assertions.assertEquals(amount, manufacturers.size());
            Assertions.assertEquals(1, manufacturers.get(0).getManId());
            Assertions.assertEquals(2, manufacturers.get(1).getManId());
            Assertions.assertEquals(3, manufacturers.get(2).getManId());
            Assertions.assertEquals(4, manufacturers.get(3).getManId());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Kiểm tra lấy thông tin nhà sản xuất khi từ khóa truyền vào là tên 1 nhà sản xuất dưới database
    // Có 1 nhà sản xuất tên "Tên nhà sản xuất thứ 1" còn hoạt động
    @Test
    public void testSelectAllManufacturerByValidKw() {
        try {
            List<Manufacturer> manufacturers = manufacturerService.getManufacturers("Tên nhà sản xuất thứ 1");
            Assertions.assertEquals(1, manufacturers.size());
            Assertions.assertEquals(1, manufacturers.get(0).getManId());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Kiểm tra lấy thông tin nhà sản xuất khi truyền vào là 1 tên nhà sản xuất không tồn tại dưới database
    // Không có nhà sản xuất nào tên "Tên nhà sản xuất thứ 9999999999999999"
    @Test
    public void testSelectAllManufacturerByInValid() {
        try {
            List<Manufacturer> manufacturers = manufacturerService.getManufacturers("Tên nhà sản xuất thứ 9999999999999999");
            Assertions.assertEquals(0, manufacturers.size());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Kiểm tra số lấy số lượng nhà sản xuất còn hoạt động dưới database
    // Có 4 nhà sản xuất dưới database nhưng chỉ có 3 nhà sản xuất còn hoạt động
    @Test
    public void testGetManufacturerAmount() {
        try {
            Manufacturer manufacturer = manufacturerServiceForTest.getManufacturerById(1);
            manufacturerService.deleteManufacturer(manufacturer);
            int amount = manufacturerService.getManufacturerAmount();
            Assertions.assertEquals(3, amount);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Kiểm tra thêm giá trị null khi thêm nhà sản xuất
    // Trả về false
    @Test
    public void testAddManufacturerWithNull() {
        try {
            Assertions.assertFalse(manufacturerService.addManufacturer(null));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Kiểm tra thêm thông tin nhà sản xuất khi thông tin không hợp lệ
    //  Trả về false
    @Test
    public void testAddManufacturerWithInvalidInformation() {
        try {
            Manufacturer manufacturer = new Manufacturer();
            manufacturer.setManName("");
            Assertions.assertFalse(manufacturerService.addManufacturer(manufacturer));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Kiểm tra thêm thông tin nhà sản xuất đã tồn tại
    // Nhà sản xuất có mã là 2 đã tồn tại. Trả về false
    @Test
    public void testAddManufacturerWithExist() {
        try {
            Manufacturer manufacturer = manufacturerServiceForTest.getManufacturerById(2);
            Assertions.assertFalse(manufacturerService.addManufacturer(manufacturer));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Kiểm tra thêm nhà sản xuất mới thành công
    // Trả về true
    @Test
    public void testAddManufacturerWithValidInformation() {
        try {
            Manufacturer manufacturer = new Manufacturer();
            manufacturer.setManName("Tên nhà sản xuất thứ 6");
            int preAmo = manufacturerService.getManufacturerAmount();
            Assertions.assertTrue(manufacturerService.addManufacturer(manufacturer));
            int nextAmo = manufacturerService.getManufacturerAmount();
            Assertions.assertNotEquals(preAmo, nextAmo);
            Manufacturer manufacturerTest = manufacturerServiceForTest.getManufacturerByName(manufacturer.getManName());
            Assertions.assertEquals(manufacturer.getManName(), manufacturerTest.getManName());
            Assertions.assertTrue(manufacturerTest.getManIsActive());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // Kiểm tra thêm giá trị null khi sửa nhà sản xuất
    // Trả về false
    @Test
    public void testUpdateManufacturerWithNull() {
        try {
            Assertions.assertFalse(manufacturerService.updateManufacturer(null));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Kiểm tra sửa thông tin nhà sản xuất khi thông tin không hợp lệ
    //  Trả về false
    @Test
    public void testUpdateManufacturerWithInvalidInformation() {
        try {
            Manufacturer manufacturer = manufacturerServiceForTest.getManufacturerById(1);
            manufacturer.setManName("");
            Assertions.assertFalse(manufacturerService.updateManufacturer(manufacturer));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Kiểm tra sửa thông tin nhà sản xuất đã tồn tại mà thông tin vừa sửa
    // trùng với thông tin nhà sản xuất khác đã tồn tại
    // Sửa thông tin nhà sản xuất 1 trùng với thông tin nhà sản xuất 2 mà
    // nhà sản xuất có mã là 2 đã tồn tại. Trả về false
    @Test
    public void testUpdateManufacturerWithExist() {
        try {
            Manufacturer manufacturer = manufacturerServiceForTest.getManufacturerById(1);
            manufacturer.setManName("Tên nhà sản xuất thứ 2");
            Assertions.assertFalse(manufacturerService.updateManufacturer(manufacturer));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Kiểm tra sửa nhà sản xuất mới thành công
    // Trả về true
    @Test
    public void testUpdateManufacturerWithValidInformation() {
        try {
            Manufacturer manufacturer = manufacturerServiceForTest.getManufacturerById(1);
            manufacturer.setManName("Tên nhà sản xuất thứ 6");
            Assertions.assertTrue(manufacturerService.updateManufacturer(manufacturer));
            Manufacturer manufacturerTest = manufacturerServiceForTest.getManufacturerByName(manufacturer.getManName());
            Assertions.assertEquals(manufacturer.getManName(), manufacturerTest.getManName());
            Assertions.assertTrue(manufacturerTest.getManIsActive());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Kiểm tra giá trị null khi xóa nhà sản xuất
    // Trả về false
    @Test
    public void testDeleteManufacturerWithNull() {
        try {
            Assertions.assertFalse(manufacturerService.deleteManufacturer(null));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Kiểm tra xóa thông tin nhà sản xuất khi thông tin không hợp lệ
    //  Trả về false
    @Test
    public void testDeleteManufacturerWithInvalidInformation() {
        try {
            Manufacturer manufacturer = manufacturerServiceForTest.getManufacturerById(1);
            manufacturer.setManName(null);
            manufacturer.setManId(null);
            Assertions.assertFalse(manufacturerService.deleteManufacturer(manufacturer));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Kiểm tra xóa thông tin nhà sản xuất không tồn tại
    // nhà sản xuất có mã là 9999 không tồn tại. Trả về false
    @Test
    public void testDeleteManufacturerWithExist() {
        try {
            Manufacturer manufacturer = manufacturerServiceForTest.getManufacturerById(1);
            manufacturer.setManId(9999);
            Assertions.assertFalse(manufacturerService.deleteManufacturer(manufacturer));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Kiểm tra xóa nhà sản xuất thành công
    // Trả về true
    @Test
    public void testDeleteManufacturerWithValidInformation() {
        try {
            Manufacturer manufacturer = manufacturerServiceForTest.getManufacturerById(1);
            int preAmo = manufacturerService.getManufacturerAmount();
            Assertions.assertTrue(manufacturerService.deleteManufacturer(manufacturer));
            int nextAmo = manufacturerService.getManufacturerAmount();
            Assertions.assertNotEquals(preAmo, nextAmo);
            Manufacturer manufacturerTest = manufacturerServiceForTest.getManufacturerByName(manufacturer.getManName());
            Assertions.assertFalse(manufacturerTest.getManIsActive());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
