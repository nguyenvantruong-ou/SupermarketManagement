package com.ou.services;

import com.ou.pojos.Staff;
import com.ou.utils.DatabaseUtils;
import com.ou.utils.MD5Utils;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class StaffServiceTest {
    private static Connection connection;
    private static StaffService staffService;
    private static StaffServiceForTest staffServiceForTest;
    public StaffServiceTest() {

    }
    @BeforeAll
    public static void setUpClass() {
        try {
            connection = DatabaseUtils.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        staffService = new StaffService();
        staffServiceForTest = new StaffServiceForTest();
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

    // kiểm tra lấy thông tin staff khi truyền vào null id
    // trả ra null object
    @Test
    public void testGetStaffByIdNull(){
        try {
            Staff staff = staffService.getStaffById(null);
            Assertions.assertNull(staff);
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    // kiểm tra lấy thông tin staff khi truyền vào id không tồn tại
    // trả ra null object
    @Test
    public void testGetStaffByIdNotExist(){
        try {
            Staff staff = staffService.getStaffById(9999999);
            Assertions.assertNull(staff);
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    // kiểm tra lấy thông tin staff khi truyền vào id tồn tại
    // trả ra limit sale object
    @Test
    public void testGetStaffByIdExist(){
        try {
            Staff staff = staffService.getStaffById(1);
            Assertions.assertNotNull(staff);
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    // kiểm tra lấy danh sách các staff với từ khóa không tồn tại
    // mong muốn: trả ra danh sách rỗng
    @Test
    public void testGetListStaffNotExist() {
        try {
            List<Staff> listStaff;
            listStaff = staffService.getListStaff("11111111111111111111111111");
            Assertions.assertEquals(0, listStaff.size());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // kiểm tra lấy danh sách các staff với từ khóa tồn tại
    // mong muốn: trả ra danh sách khác rỗng
    @Test
    public void testGetListStaffExist() {
        try {
            List<Staff> listStaff;
            listStaff = staffService.getListStaff("Bình");
            Assertions.assertNotEquals(0, listStaff.size());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // kiểm tra tìm kiếm với chuỗi rỗng
    // mong muốn trả ra số lượng tất cả các staff
    @Test
    public void testGetListStaffExist2() {
        try {
            List<Staff> listStaff1;
            listStaff1 = staffService.getListStaff("");

            List<Staff> listSatff2;
            listSatff2 = staffServiceForTest.getListUsernameStaff();

            Assertions.assertEquals(listStaff1.size(),listSatff2.size());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // kiểm tra lấy danh sách tên các nhánh
    // mong muốn: danh sách khác rỗng
    @Test
    public void testGetListBranch() {
        try {
            List<String> listBra = staffService.getListBranch();
            Assertions.assertNotEquals(0, listBra.size());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // kiểm tra username có tồn tại hay chưa
    // mong muốn trả ra false
    @Test
    public void testUsernameNotExist() {
        try {
            boolean flag = staffService.isUsername("1111111111111111111111");
            Assertions.assertFalse(flag);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // kiểm tra username có tồn tại hay chưa
    // mong muốn trả ra false
    @Test
    public void testUsernameNotExist2() {
        try {
            boolean flag = staffService.isUsername("");
            Assertions.assertFalse(flag);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // kiểm tra username có tồn tại hay chưa
    // mong muốn trả ra true
    @Test
    public void testUsernameExist() {
        try {
            boolean flag = staffService.isUsername("username1");
            Assertions.assertTrue(flag);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // kiểm tra username: username1 có đang hoạt động ?
    // mong muốn trả ra true
    @Test
    public void testIsActivePass() {
        try {
            boolean flag = staffService.isActive("username1");
            Assertions.assertTrue(flag);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // kiểm tra active của username không tồn tại
    // mong muốn trả ra false
    @Test
    public void testIsActiveFailed() {
        try {
            boolean flag = staffService.isActive("111111111111111");
            Assertions.assertFalse(flag);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // kiểm tra active của username null
    // mong muốn trả ra false
    @Test
    public void testIsActiveFailed2() {
        try {
            boolean flag = staffService.isActive("");
            Assertions.assertFalse(flag);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // sửa active cho tài khoản có active = fasle               username10 : ngưng hoạt động
    // mong muốn trả ra true và active = true
    @Test
    public void testSetActiveForUsername() {
        try {
            String user_name = "username10";
            boolean flag = staffService.setActive(user_name);
            Assertions.assertTrue(flag);

            Assertions.assertEquals(true, staffService.isActive(user_name));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // sửa active cho tài khoản không tồn tại
    // mong muốn trả ra true và active = false
    @Test
    public void testSetActiveForUsernameFailed() {
        try {
            String user_name = "";
            boolean flag = staffService.setActive(user_name);
            Assertions.assertFalse(flag);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // kiểm tra thêm staff không rỗng
    // mong muốn trả ra false
    @Test
    public void testAddStaffFailed() {
        try {
            Staff staff = new Staff();
            boolean flag = staffService.addStaff(staff);
            Assertions.assertFalse(flag);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // chạy pass nếu lưu dữ liệu dưới database
    // kiểm tra thêm staff hợp lệ
    // mong muốn trả ra true và số lượng sau khi thêm = số lượng trước + 1
    @Test
    public void testAddStaffPass() {
        try {
            Integer before = staffServiceForTest.getListUsernameStaff().size();
            Staff staff = new Staff();
            staff.setPersLastName("nguyen van");
            staff.setPersFirstName("truong");
            staff.setPersIdCard("206442815");
            staff.setPersPhoneNumber("0356371760");
            staff.setPersSex(Byte.parseByte("1"));
            Date dateOB = new Date();
            staff.setPersDateOfBirth(dateOB);
            staff.setStaIsAdmin(false);
            staff.setBranchName("Tên chi nhánh thứ 1");
            staff.setStaUsername("truong1703");
            staff.setStaPassword("12345678");

            // kiểm tra thêm staff
            boolean flag = staffService.addStaff(staff);
            Assertions.assertTrue(flag);

            // kiểm tra số lượng sau khi thêm
            Integer after = staffServiceForTest.getListUsernameStaff().size();

            Assertions.assertEquals(after, before +1);

            // kiểm tra thông tin mới thêm
            Staff staffTest = new Staff();
            staffTest = staffService.getStaffByUsername(staff.getStaUsername());

            Assertions.assertEquals(staff.getBranchName(), staffTest.getBranch().getBraName());
            Assertions.assertEquals(staff.getPersLastName(), staffTest.getPersLastName());
            Assertions.assertEquals(staff.getPersFirstName(), staffTest.getPersFirstName());
            Assertions.assertEquals(staff.getPersIdCard(), staffTest.getPersIdCard());
            Assertions.assertEquals(staff.getPersPhoneNumber(), staffTest.getPersPhoneNumber());
            Assertions.assertEquals(staff.getPersSex(), staffTest.getPersSex());
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            String date = formatter.format(staff.getPersDateOfBirth());
            boolean dateOfBirth = date.compareTo(staffTest.getPersDateOfBirth().toString()) == 0 ? true : false;
            Assertions.assertTrue(dateOfBirth);
            boolean isAdmin = staffServiceForTest.isAdminByUsername(staff.getStaUsername());
            Assertions.assertFalse(isAdmin);
            Assertions.assertEquals(staff.getStaUsername(), staffTest.getStaUsername());
            String password = staffServiceForTest.getPasswordStaff(staff.getStaUsername());
            Assertions.assertEquals(staff.getStaPassword(), password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // kiểm tra số CMND có tồn tại không với dữ liệu truyền vào rỗng
    // mong muốn trả ra fasle
    @Test
    public void testCardId() {
        try {
            Assertions.assertFalse(staffService.isExistCardId(""));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // kiểm tra số CMND không tồn tại
    // mong muốn trả ra fasle
    @Test
    public void testCardIdNotExist() {
        try {
            Assertions.assertFalse(staffService.isExistCardId("11111111111111111"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // kiểm tra số CMND rỗng
    // mong muốn trả ra fasle
    @Test
    public void testCardIdFailed() {
        try {
            Assertions.assertFalse(staffService.isExistCardId(""));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // kiểm tra số CMND có tồn tại không với dữ liệu truyền vào rỗng       database có pers_id_card : 206442815
    // mong muốn trả ra true
    @Test
    public void testCardIdExist() {
        try {
            Assertions.assertTrue(staffService.isExistCardId("206442815"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // Lấy staff với id truyền vào không tồn tại
    // trả ra null
    @Test
    public void testGetStaffDataByIdNotExist() {
        try {
            Staff staff = staffService.getStaffById(9999999);
            Assertions.assertNull(staff);
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    // Lấy staff với id truyền vào  tồn tại
    // trả ra staff khác null
    @Test
    public void testGetStaffDataByIdExist() {
        try {
            Staff staff = staffService.getStaffById(1);
            Assertions.assertNotNull(staff);
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    // cập nhật staff không hợp lệ
    // trả ra false
    @Test
    public void testUpdateStaffFailed() {
        Staff staff = new Staff();
        boolean flag = staffService.updateStaff(staff);
        Assertions.assertFalse(flag);
    }

    // cập nhật staff hợp lệ
    // trả ra true
    @Test
    public void testUpdateStaffPass() {
        Staff staff = new Staff();
        staff.setPersId(1);
        staff.setPersLastName("nguyen van");
        staff.setPersFirstName("truong");
        staff.setPersIdCard("1111111111");
        staff.setPersPhoneNumber("0356371760");
        staff.setPersSex(Byte.parseByte("1"));
        Date dateOB = new Date();
        staff.setPersDateOfBirth(dateOB);
        staff.setBranchName("Tên chi nhánh thứ 1");
        staff.setStaIsAdmin(true);
        staff.setStaUsername("username1");

        boolean flag = staffService.updateStaff(staff);
        Assertions.assertTrue(flag);

        // kiểm tra thông tin mới sửa
        Staff staffTest = new Staff();
        try {
            staffTest = staffService.getStaffByUsername(staff.getStaUsername());
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Assertions.assertEquals(staff.getBranchName(), staffTest.getBranch().getBraName());
        Assertions.assertEquals(staff.getPersLastName(), staffTest.getPersLastName());
        Assertions.assertEquals(staff.getPersFirstName(), staffTest.getPersFirstName());
        Assertions.assertEquals(staff.getPersPhoneNumber(), staffTest.getPersPhoneNumber());
        Assertions.assertEquals(staff.getPersSex(), staffTest.getPersSex());
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String date = formatter.format(staff.getPersDateOfBirth());
        boolean dateOfBirth = date.compareTo(staffTest.getPersDateOfBirth().toString()) == 0 ? true : false;
        Assertions.assertTrue(dateOfBirth);
        boolean isAdmin = staffServiceForTest.isAdminByUsername(staff.getStaUsername());
        Assertions.assertTrue(isAdmin);
        Assertions.assertEquals(staff.getStaUsername(), staffTest.getStaUsername());
    }

    // reset password cho username rỗng
    // trả ra false
    @Test
    public void testResetPasswordFailed() {
        boolean flag = staffService.resetPassword("");
        Assertions.assertFalse(flag);
    }

    // reset password cho username không tồn tại
    // trả ra false
    @Test
    public void testResetPasswordFailed2() {
        boolean flag = staffService.resetPassword("99999999999");
        Assertions.assertFalse(flag);
    }

    // reset password cho username  tồn tại
    // trả ra true
    @Test
    public void testResetPasswordPass() {
        boolean flag = staffService.resetPassword("username1");
        Assertions.assertTrue(flag);
    }

    // reset password cho username  tồn tại
    // mong muốn mật khẩu lưu dưới database là mã băm MD5 username
    @Test
    public void testResetPasswordPass2() {
        String user_name = "username1";
        boolean flag = staffService.resetPassword(user_name);
        Assertions.assertTrue(flag);

        String md5 = MD5Utils.getMd5(user_name);
        String password = "";
        try {
            password = staffServiceForTest.getPasswordStaff(user_name);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Assertions.assertEquals(md5, password);
    }

    // xóa staff không tồn tại
    // trả ra false
    @Test
    public void testDeleteStaffFailed() {
        boolean flag = staffService.deleteSatff(999999999);
        Assertions.assertFalse(flag);
    }

    // xóa staff không tồn tại
    // trả ra true và active sau khi xóa là false
    @Test
    public void testDeleteStaffPass() {
        Integer id = 1;
        boolean flag = staffService.deleteSatff(id);
        Assertions.assertTrue(flag);

        boolean active =false;
        try {
            active = staffServiceForTest.isActiveById(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Assertions.assertEquals(false, active);
    }
}
