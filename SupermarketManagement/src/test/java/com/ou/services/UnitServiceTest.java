package com.ou.services;

import com.ou.pojos.Unit;
import com.ou.utils.DatabaseUtils;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class UnitServiceTest {
    private static Connection connection;
    private static UnitService unitService;
    private static UnitServiceForTest unitServiceForTest;

    public UnitServiceTest() {

    }

    @BeforeAll
    public static void setUpClass() {
        try {
            connection = DatabaseUtils.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        unitService = new UnitService();
        unitServiceForTest = new UnitServiceForTest();
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

    // Kiểm tra lấy thông tin đơn vị khi từ khóa truyền vào là null
    // Phải trả về tất cả các đơn vị của cửa hàng  (4 đơn vị)
    @Test
    public void testSelectAllUnitByNullKw() {
        try {
            List<Unit> units = unitService.getUnits(null);
            int amount = unitService.getUnitAmount();
            Assertions.assertEquals(amount, units.size());
            Assertions.assertEquals(1, units.get(0).getUniId());
            Assertions.assertEquals(2, units.get(1).getUniId());
            Assertions.assertEquals(3, units.get(2).getUniId());
            Assertions.assertEquals(4, units.get(3).getUniId());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Kiểm tra lấy thông tin đơn vị khi từ khóa truyền vào là 1 chuỗi rỗng
    // Phải trả về tất cả các đơn vị của cửa hàng (4 đơn vị)
    @Test
    public void testSelectAllUnitByEmptyKw() {
        try {
            List<Unit> units = unitService.getUnits("");
            int amount = unitService.getUnitAmount();
            Assertions.assertEquals(amount, units.size());
            Assertions.assertEquals(1, units.get(0).getUniId());
            Assertions.assertEquals(2, units.get(1).getUniId());
            Assertions.assertEquals(3, units.get(2).getUniId());
            Assertions.assertEquals(4, units.get(3).getUniId());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Kiểm tra lấy thông tin đơn vị khi từ khóa truyền vào là 1 chuỗi khoảng trắng
    // Phải trả về 0
    @Test
    public void testSelectAllUnitBySpacesKw() {
        try {
            List<Unit> units = unitService.getUnits("        ");
            Assertions.assertEquals(0, units.size());
            int amount = unitService.getUnitAmount();
            Assertions.assertEquals(amount, units.size());
            Assertions.assertEquals(1, units.get(0).getUniId());
            Assertions.assertEquals(2, units.get(1).getUniId());
            Assertions.assertEquals(3, units.get(2).getUniId());
            Assertions.assertEquals(4, units.get(3).getUniId());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Kiểm tra lấy thông tin đơn vị khi từ khóa truyền vào là tên 1 nhà sản xuất dưới database
    // Có 1 đơn vị tên "Tên đơn vị thứ 1"
    @Test
    public void testSelectAllUnitByValidKw() {
        try {
            List<Unit> units = unitService.getUnits("Tên đơn vị thứ 1");
            Assertions.assertEquals(1, units.size());
            Assertions.assertEquals(1, units.get(0).getUniId());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Kiểm tra lấy thông tin đơn vị khi truyền vào là 1 tên đơn vị không tồn tại dưới database
    // Không có đơn vị nào nào tên "Tên đơn vị thứ 9999999999999999"
    @Test
    public void testSelectAllManufacturerByInValid() {
        try {
            List<Unit> units = unitService.getUnits("Tên đơn vị thứ 9999999999999999");
            Assertions.assertEquals(0, units.size());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Kiểm tra số lấy số lượng đơn vị còn hoạt động dưới database
    // Có 4 đơn vị dưới database nhưng chỉ có 3 đơn vị còn hoạt động
    @Test
    public void testGetManufacturerAmount() {
        try {
            Unit unit = unitServiceForTest.getUnitById(1);
            unitService.deleteUnit(unit);
            int amount = unitService.getUnitAmount();
            Assertions.assertEquals(3, amount);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Kiểm tra thêm giá trị null khi thêm đơn vị
    // Trả về false
    @Test
    public void testAddUnitWithNull() {
        try {
            Assertions.assertFalse(unitService.addUnit(null));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Kiểm tra thêm thông tin đơn vị khi thông tin không hợp lệ
    //  Trả về false
    @Test
    public void testAddUnitWithInvalidInformation() {
        try {
            Unit unit = new Unit();
            unit.setUniName("");
            Assertions.assertFalse(unitService.addUnit(unit));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Kiểm tra thêm thông tin đơn vị đã tồn tại
    // Đơn vị có mã là 2 đã tồn tại. Trả về false
    @Test
    public void testAddManufacturerWithExist() {
        try {
            Unit unit = unitServiceForTest.getUnitById(2);
            Assertions.assertFalse(unitService.addUnit(unit));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Kiểm tra thêm đơn vị mới thành công
    // Trả về true
    @Test
    public void testAddUnitWithValidInformation() {
        try {
            Unit unit = new Unit();
            unit.setUniName("Tên đơn vị thứ 5");
            int preAmo = unitService.getUnitAmount();
            Assertions.assertTrue(unitService.addUnit(unit));
            int nextAmo = unitService.getUnitAmount();
            Assertions.assertNotEquals(preAmo, nextAmo);
            Unit unitTest = unitServiceForTest.getUnitByName(unit.getUniName());
            Assertions.assertEquals(unit.getUniName(), unitTest.getUniName());
            Assertions.assertTrue(unitTest.getUniIsActive());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // Kiểm tra thêm giá trị null khi sửa đơn vị
    // Trả về false
    @Test
    public void testUpdateUnitWithNull() {
        try {
            Assertions.assertFalse(unitService.updateUnit(null));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Kiểm tra sửa thông tin đơn vị khi thông tin không hợp lệ
    //  Trả về false
    @Test
    public void testUpdateUnitWithInvalidInformation() {
        try {
            Unit unit = unitServiceForTest.getUnitById(1);
            unit.setUniName("");
            Assertions.assertFalse(unitService.updateUnit(unit));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Kiểm tra sửa thông tin đơn vị đã tồn tại mà thông tin vừa sửa
    // trùng với thông tin đơn vị khác đã tồn tại
    // Sửa thông tin đơn vị 1 trùng với thông tin đơn vị 2 mà
    // đơn vị có mã là 2 đã tồn tại. Trả về false
    @Test
    public void testUpdateUnitWithExist() {
        try {
            Unit unit = unitServiceForTest.getUnitById(1);
            unit.setUniName("Tên đơn vị thứ 2");
            Assertions.assertFalse(unitService.updateUnit(unit));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Kiểm tra sửa đơn vị mới thành công
    // Trả về true
    @Test
    public void testUpdateUnitWithValidInformation() {
        try {
            Unit unit = unitServiceForTest.getUnitById(1);
            unit.setUniName("Tên đơn vị thứ 5");
            Assertions.assertTrue(unitService.updateUnit(unit));
            Unit unitTest = unitServiceForTest.getUnitByName(unit.getUniName());
            Assertions.assertEquals(1, unitTest.getUniId());
            Assertions.assertTrue(unitTest.getUniIsActive());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Kiểm tra giá trị null khi xóa đơn vị
    // Trả về false
    @Test
    public void testDeleteUnitWithNull() {
        try {
            Assertions.assertFalse(unitService.deleteUnit(null));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Kiểm tra xóa thông tin đơn vị khi thông tin không hợp lệ
    //  Trả về false
    @Test
    public void testDeleteUnitWithInvalidInformation() {
        try {
            Unit unit = unitServiceForTest.getUnitById(1);
            unit.setUniName(null);
            unit.setUniId(null);
            Assertions.assertFalse(unitService.deleteUnit(unit));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Kiểm tra xóa thông tin đơn vị không tồn tại
    // đơn vị có mã là 9999 không tồn tại. Trả về false
    @Test
    public void testDeleteUnitWithExist() {
        try {
            Unit unit = unitServiceForTest.getUnitById(1);
            unit.setUniId(9999);
            Assertions.assertFalse(unitService.deleteUnit(unit));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Kiểm tra xóa đơn vị thành công
    // Trả về true
    @Test
    public void testDeleteUnitWithValidInformation() {
        try {
            Unit unit = unitServiceForTest.getUnitById(1);
            int preAmo = unitService.getUnitAmount();
            Assertions.assertTrue(unitService.deleteUnit(unit));
            int nextAmo = unitService.getUnitAmount();
            Assertions.assertNotEquals(preAmo, nextAmo);
            Unit unitTest = unitServiceForTest.getUnitByName(unit.getUniName());
            Assertions.assertFalse(unitTest.getUniIsActive());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
