package com.ou.services;

import com.ou.pojos.Sale;
import com.ou.pojos.SalePercent;
import com.ou.utils.DatabaseUtils;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SaleServiceTest {
    private static Connection connection;
    private static SaleService saleService;
    private static SaleServiceForTest saleServiceForTest;
    public SaleServiceTest() {

    }
    @BeforeAll
    public static void setUpClass() {
        try {
            connection = DatabaseUtils.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        saleService = new SaleService();
        saleServiceForTest = new SaleServiceForTest();
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

    private List<Integer> getSaleIds(){
        List<Integer> saleIds = new ArrayList<>();
        for(int i=1; i<=8; i++)
            saleIds.add(i);
        return saleIds;
    }

    // kiểm tra lấy danh sách sale khi truyền vào kw rỗng
    // trả về tất cả mã giảm giá còn hoạt dộng
    // có 8 mã giảm giá còn hoạt động dưới db
    @Test
    public void testGetListSaleByEmptyKw(){
        try {
            List<Sale> sales = saleService.getSales("");
            List<Integer> saleIds = getSaleIds();
            Assertions.assertEquals(8, sales.size());
            for(int i=0; i<sales.size(); i++){
                Assertions.assertEquals(saleIds.get(i), sales.get(i).getSaleId());
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    // kiểm tra lấy thông tin sale khi truyền vào null id
    // trả ra null object
    @Test
    public void testGetSaleByIdNull(){
        try {
            Sale sale = saleService.getSaleById(null);
            Assertions.assertNull(sale);
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    // kiểm tra lấy thông tin sale khi truyền vào id không tồn tại
    // trả ra null object
    @Test
    public void testGetSaleByIdNotExist(){
        try {
            Sale sale = saleService.getSaleById(9999999);
            Assertions.assertNull(sale);
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    // kiểm tra lấy thông tin sale khi truyền vào id tồn tại
    // trả ra sale object
    @Test
    public void testGetSaleByIdExist(){
        try {
            Sale sale = saleService.getSaleById(5);
            Assertions.assertNotNull(sale);
            Assertions.assertEquals(5, sale.getSaleId());
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    // kiểm tra thêm thông tin sale khi truyền vào sale là null
    // trả về false
    @Test
    public void testAddSaleBySaleIsNull(){
        try {
            Sale sale = null;
            Assertions.assertFalse(saleService.addSale(sale));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // kiểm tra thêm thông tin sale khi truyền vào hợp lệ
    // trả về true
    @Test
    public void testAddSaleByInfoValid(){
        try {
            int preAmount = saleService.getSaleAmount();
            Sale sale = new Sale();
            SalePercent salePercent = new SalePercent();
            salePercent.setSperId(1);
            salePercent.setSperIsActive(true);
            sale.setSalePercent(salePercent);
            Assertions.assertTrue(saleService.addSale(sale));
            int nextAmount = saleService.getSaleAmount();
            Assertions.assertNotEquals(preAmount, nextAmount);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // kiểm tra sửa thông tin sale khi truyền vào sale là null
    // trả về false
    @Test
    public void testUpdateSaleBySaleIsNull(){
        try {
            Sale sale = null;
            Assertions.assertFalse(saleService.updateSale(sale));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // kiểm tra thêm thông tin sale khi truyền vào hợp lệ
    // trả về true
    @Test
    public void testUpdateSaleByInfoValid(){
        try {
            Sale sale = saleServiceForTest.getSaleById(1);
            sale.getSalePercent().setSperId(3);
            Assertions.assertTrue(saleService.updateSale(sale));
            Sale saleUpdate = saleServiceForTest.getSaleById(1);
            Assertions.assertEquals(3, saleUpdate.getSalePercent().getSperId());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // kiểm tra xóa thông tin sale khi truyền vào sale_id là null
    // trả về false
    @Test
    public void testDeleteSaleByIdIsNull(){
        try {
            Sale sale = new Sale();
            sale.setSaleId(null);
            Assertions.assertFalse(saleService.deleteSale(sale));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // kiểm tra xóa thông tin sale khi truyền vào sale_id hợp lệ
    // trả về true
    @Test
    public void testDeleteSaleByIdIsValid(){
        try {
            int preAmount = saleService.getSaleAmount();
            Sale sale = saleServiceForTest.getSaleById(3);
            Assertions.assertTrue(saleService.deleteSale(sale));
            int nextAmount = saleService.getSaleAmount();
            Assertions.assertNotEquals(preAmount, nextAmount);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
