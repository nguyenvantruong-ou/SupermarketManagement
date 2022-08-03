package com.ou.services;

import com.ou.pojos.SalePercent;
import com.ou.utils.DatabaseUtils;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class SalePercentServiceTest {
    private static Connection connection;
    private static SalePercentService salePercentService;
    private static SalePercentServiceForTest salePercentServiceForTest;
    public SalePercentServiceTest() {

    }
    @BeforeAll
    public static void setUpClass() {
        try {
            connection = DatabaseUtils.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        salePercentService = new SalePercentService();
        salePercentServiceForTest = new SalePercentServiceForTest();
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

    // kiểm tra lấy danh sách mã giảm giá khi truyền vào % giảm giá không tồn tại
    // trả ra 0
    @Test
    public void testGetSalePercentByPercentNotExist(){
        try {
            List<SalePercent> salePercents = salePercentService.getSalePercents(9);
            Assertions.assertEquals(0, salePercents.size());
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    // kiểm tra lấy danh sách mã giảm giá khi truyền vào % giảm giá tồn tại
    // trả ra danh sách mã giảm giá
    @Test
    public void testGetSalePercentByPercentExist(){
        try {
            List<SalePercent> salePercents = salePercentService.getSalePercents(119);
            Assertions.assertNotNull(salePercents);
        }catch (SQLException e){
            e.printStackTrace();
        }
    }


    // kiểm tra thêm thông tin mã giảm giá khi truyền vào hợp lệ
    // trả về true
    @Test
    public void testAddSalePercentBySperIdIsValid(){
        try {
            SalePercent salePercent = new SalePercent();
            salePercent.setSperId(2);
            salePercent.setSperPercent(3);
            salePercent.setSperIsActive(true);
            Assertions.assertTrue(salePercentService.addSalePercent(salePercent));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Kiểm tra thêm thông tin mã giảm giá khi thông tin không hợp lệ
    //  Trả về false
    @Test
    public void testAddSalePercentWithInvalidInformation() {
        try {
            SalePercent salePercent = new SalePercent();
            salePercent.setSperPercent(-2);
            Assertions.assertFalse(salePercentService.addSalePercent(salePercent));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Kiểm tra thêm thông tin mã giảm giá đã tồn tại
    // Mã giảm giá có % giảm giá là 2 đã tồn tại. Trả về false
    @Test
    public void testAddSalePercentWithExist() {
        try {
            SalePercent salePercent = salePercentService.getSalePercentById(2);
            Assertions.assertFalse(salePercentService.addSalePercent(salePercent));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // kiểm tra sửa thông tin mã giảm giá khi truyền vào là null
    // trả về false
    @Test
    public void testUpdateSalePercentBySperIdIsNull(){
        try {
            SalePercent salePercent =  null;
            Assertions.assertFalse(salePercentService.updateSalePercent(salePercent));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Kiểm tra sửa thông tin mã giảm giá không hợp lệ
    // Trả về false
    @Test
    public void testUpdateSalePercentWithInvalidInformation() {
        try {
            SalePercent salePercent = salePercentServiceForTest.getSalePercentById(2);
            salePercent.setSperPercent(-11);
            Assertions.assertFalse(salePercentService.updateSalePercent(salePercent));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Kiểm tra sửa thông tin mã giảm giá  hợp lệ
    // Trả về true
    @Test
    public void testUpdateSalePercentWithValidInformation() {
        try {
            SalePercent salePercent = salePercentServiceForTest.getSalePercentById(2);
            salePercent.setSperPercent(-111);
            Assertions.assertTrue(salePercentService.updateSalePercent(salePercent));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // kiểm tra xóa thông tin sale khi truyền vào là null
    // trả về false
    @Test
    public void testDeleteSaleByIdIsNull(){
        try {
            SalePercent salePercent =  null;
            Assertions.assertFalse(salePercentService.deleteSalePercent(salePercent));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // kiểm tra xóa thông tin sale khi truyền vào sper_percent hợp lệ
    // trả về true
    @Test
    public void testDeleteSaleByPercentIsValid(){
        try {
            SalePercent salePercent =  salePercentServiceForTest.getSalePercentByPercent(12);
            Assertions.assertTrue(salePercentService.deleteSalePercent(salePercent));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // kiểm tra xóa thông tin sale khi truyền vào sper_percent không  tồn tại
    // trả về false
    @Test
    public void testDeleteSaleWithExist(){
        try {
            SalePercent salePercent =  salePercentServiceForTest.getSalePercentByPercent(999);
            Assertions.assertFalse(salePercentService.deleteSalePercent(salePercent));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Kiểm tra xóa mã giảm giá thành công
    // Trả về true
    @Test
    public void testDeleteSalePercentWithValidInformation() {
        try {
            SalePercent salePercent =  salePercentServiceForTest.getSalePercentByPercent(10);
            int preAmo = salePercentService.getSalePercentAmount();
            Assertions.assertTrue(salePercentService.deleteSalePercent(salePercent));
            int nextAmo = salePercentService.getSalePercentAmount();
            Assertions.assertNotEquals(preAmo, nextAmo);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Kiểm tra số lấy số lượng % giảm giá còn hoạt động dưới database
    // Có 11 % giảm giá dưới database nhưng chỉ có 8 % giảm giá còn hoạt động
    @Test
    public void testGetSalePercentAmount() {
        try {
            int amount = salePercentService.getSalePercentAmount();
            Assertions.assertEquals(8, amount);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
