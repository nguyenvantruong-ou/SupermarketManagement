package com.ou.services;

import com.ou.pojos.LimitSale;
import com.ou.utils.DatabaseUtils;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class LimitSaleServiceTest {
    private static Connection connection;
    private static LimitSaleService limitSaleService;
    private static LimitSaleServiceForTest limitSaleServiceForTest;
    public LimitSaleServiceTest() {

    }
    @BeforeAll
    public static void setUpClass() {
        try {
            connection = DatabaseUtils.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        limitSaleService = new LimitSaleService();
        limitSaleServiceForTest = new LimitSaleServiceForTest();
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

    // lấy danh sách limit sale id
    // có 3 limitsale id 5, 7, 8 dưới db
    private List<Integer> getLimitSaleIds(){
        List<Integer> limitSaleIds = new ArrayList<>();
        limitSaleIds.add(7);
        limitSaleIds.add(8);
        limitSaleIds.add(5);
        return limitSaleIds;
    }
    // kiểm tra lấy thông tin limit sale khi truyền vào null id
    // trả ra null object
    @Test
    public void testGetLimitSaleByIdNull(){
        try {
            LimitSale limitSale = limitSaleService.getLimitSaleById(null);
            Assertions.assertNull(limitSale);
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    // kiểm tra lấy thông tin limit sale khi truyền vào id không tồn tại
    // trả ra null object
    @Test
    public void testGetLimitSaleByIdNotExist(){
        try {
            LimitSale limitSale = limitSaleService.getLimitSaleById(99999999);
            Assertions.assertNull(limitSale);
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    // kiểm tra lấy thông tin limit sale khi truyền vào id tồn tại
    // trả ra limit sale object
    @Test
    public void testGetLimitSaleByIdExist(){
        try {
            LimitSale limitSale = limitSaleService.getLimitSaleById(5);
            Assertions.assertNotNull(limitSale);
            Assertions.assertEquals(5, limitSale.getSaleId());
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    // kiểm tra lấy danh sách limit sale khi truyền vào ""
    // trả về tất cả limit sale
    @Test
    public void testGetListLimitSaleWithEmptyKw(){
        try {
            List<Integer> limitSaleIds = getLimitSaleIds();
            List<LimitSale> limitSales = limitSaleService.getLimitSales();
            Assertions.assertNotNull(limitSales);
            Assertions.assertEquals(3, limitSales.size());
            for(int i=0; i<limitSales.size(); i++)
                Assertions.assertEquals(limitSaleIds.get(i), limitSales.get(i).getSaleId());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // kiểm tra lây danh sách limit sale khi truyền vào 1 ngày
    // trả về tất cả limit sale mà ngày truyền nào nằm trong khoảng fromdate và todate
    // trả về 2 limit sale thoả mãn.
    @Test
    public void testGetListLimitSaleWithDate(){
        try {
            List<LimitSale> limitSales = limitSaleService.getLimitSales(Date.valueOf("2022-02-05"));
            Assertions.assertNotNull(limitSales);
            Assertions.assertEquals(2, limitSales.size());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    // kiểm tra thêm 1 limit sale với thông tin không hợp lệ (limitsale = null)
    // trả về false
    @Test
    public void testAddLimitSaleWithLimitSaleIsNull(){
        try {
            LimitSale limitSale = null;
            Assertions.assertFalse(limitSaleService.addLimitSale(limitSale,1));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    // kiểm tra thêm 1 limit sale khi is_active = true và không truyền vào product_id
    // trả về false
    @Test
    public void testAddLimitSaleWithoutProId(){
        try {
            LimitSale limitSale = new LimitSale();
            limitSale.setSaleId(5);
            limitSale.setSaleIsActive(true);
            Assertions.assertFalse(limitSaleService.addLimitSale(limitSale,0));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    // kiểm tra thêm 1 limit sale khi is_active = false và không truyền vào product_id
    // bật lại cờ active cho limit sale
    // trả về true
    @Test
    public void testAddLimitSaleWithoutProId2(){
        try {
            LimitSale limitSale = new LimitSale();
            limitSale.setSaleId(5);
            limitSale.setSaleIsActive(false);
            Assertions.assertTrue(limitSaleService.addLimitSale(limitSale,0));

            LimitSale limitSale2 = limitSaleService.getLimitSaleByLsalId(5);
            Assertions.assertTrue(limitSale2.getSaleIsActive());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    // kiểm tra thêm 1 limit sale mới với thông tin hợp lệ
    // trả về true
    @Test
    public void testAddLimitSaleWithInfoValid(){
        try {
            int preAmount = limitSaleService.getTotalAmountLimitSale();

            LimitSale limitSale = new LimitSale();
            limitSale.setSaleId(1);
            limitSale.setLsalFromDate(Date.valueOf("2022-04-12"));
            limitSale.setLsalToDate(Date.valueOf("2022-05-12"));
            Assertions.assertTrue(limitSaleService.addLimitSale(limitSale,3));

            int nextAmount = limitSaleService.getTotalAmountLimitSale();
            Assertions.assertNotEquals(preAmount, nextAmount);

            LimitSale limitSaleNew = limitSaleService.getLimitSaleById(1);
            Assertions.assertEquals(limitSale.getLsalFromDate(), limitSaleNew.getLsalFromDate());
            Assertions.assertEquals(limitSale.getLsalToDate(), limitSaleNew.getLsalToDate());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    // kiểm tra thêm 1 limit sale (đã có) với thông tin sản phẩm hợp lệ
    // trả về true
    @Test
    public void testAddLimitSaleWithInfoValid2(){
        try {
            int preAmount = limitSaleService.getTotalAmountLimitSale();

            LimitSale limitSale = new LimitSale();
            limitSale.setSaleId(5);
            Assertions.assertTrue(limitSaleService.addLimitSale(limitSale,3));

            int nextAmount = limitSaleService.getTotalAmountLimitSale();
            Assertions.assertEquals(preAmount, nextAmount);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Kiểm tra cập nhật 1 limit sale với thông tin không hợp lệ
    // truyền vào mã limit sale không hợp lệ: 3 (không tồn tại limitsale_id là 3 dưới db)
    // kết quả false
    @Test
    public void testUpdateLimitSaleWithInfoInValid(){
        try {
            LimitSale limitSale = new LimitSale();
            limitSale.setSaleId(3);
            limitSale.setLsalFromDate(Date.valueOf("2022-02-02"));
            limitSale.setLsalToDate(Date.valueOf("2022-03-02"));
            Assertions.assertFalse(limitSaleService.updateLimitSale(limitSale));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // kiểm tra cập nhật limit sale với thông tin hợp lệ
    // kết quả là true
    @Test
    public void testUpdateLimitSaleWithInfoValid(){
        try {
            LimitSale limitSale = new LimitSale();
            limitSale.setSaleId(5);
            limitSale.setLsalFromDate(Date.valueOf("2022-04-16"));
            limitSale.setLsalToDate(Date.valueOf("2022-05-16"));
            Assertions.assertTrue(limitSaleService.updateLimitSale(limitSale));

            LimitSale limitSaleUpdate = limitSaleService.getLimitSaleByLsalId(5);
            Assertions.assertEquals(limitSale.getLsalFromDate(), limitSaleUpdate.getLsalFromDate());
            Assertions.assertEquals(limitSale.getLsalToDate(), limitSaleUpdate.getLsalToDate());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // kiểm tra xóa limit sale không hợp lệ
    // limit sale id = 2 không tồn tại dưới db
    // trả về false
    @Test
    public void testDeleteLimitSaleWithInfoInValid(){
        try {
            LimitSale limitSale = new LimitSale();
            limitSale.setSaleId(2);
            Assertions.assertFalse(limitSaleService.deleteLimitSale(limitSale, 1));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // kiểm tra xóa limit sale không hợp lệ
    // limit sale = null
    // trả về false
    @Test
    public void testDeleteLimitSaleWithInfoInValid2(){
        try {
            LimitSale limitSale = null;
            Assertions.assertFalse(limitSaleService.deleteLimitSale(limitSale, 1));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // kiểm tra xóa limit sale với id hợp lệ và có truyền product_id
    // limit sale id = 5 có sản phẩm id = 1 dưới db
    // trả về true
    @Test
    public void testDeleteLimitSaleWithInfoValid(){
        try {
            List<String> preAmount = limitSaleService.getIdProductByLsalId(5);
            LimitSale limitSale = new LimitSale();
            limitSale.setSaleId(5);
            Assertions.assertTrue(limitSaleService.deleteLimitSale(limitSale, 1));

            List<String> nextAmount = limitSaleService.getIdProductByLsalId(5);
            Assertions.assertNotEquals(preAmount, nextAmount);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // kiểm tra xóa limit sale với id hợp lệ không truyền vào product_id
    // limit sale id = 5
    // trả về true khi xoá
    // trả về false ở trường active
    @Test
    public void testDeleteLimitSaleWithInfoValid2(){
        try {
            LimitSale limitSale = new LimitSale();
            limitSale.setSaleId(5);
            Assertions.assertTrue(limitSaleService.deleteLimitSale(limitSale, 0));

            LimitSale limitSaleUpdate = limitSaleService.getLimitSaleByLsalId(5);
            Assertions.assertFalse(limitSaleUpdate.getSaleIsActive());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
