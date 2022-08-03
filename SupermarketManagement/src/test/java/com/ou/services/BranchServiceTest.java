package com.ou.services;

import com.ou.pojos.Branch;
import com.ou.utils.DatabaseUtils;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class BranchServiceTest {
    private static Connection connection;
    private static BranchService branchService;
    private static BranchServiceForTest branchServiceForTest;

    public BranchServiceTest() {

    }

    @BeforeAll
    public static void setUpClass() {
        try {
            connection = DatabaseUtils.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        branchService = new BranchService();
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

    // Kiểm tra lấy thông tin chi nhánh khi từ khóa truyền vào là null
    // Phải trả về tất cả các chi nhánh(2 chi nhánh)
    @Test
    public void testSelectAllBranchByNullKw() {
        try {
            List<Branch> branches = branchService.getBranches(null);
            Assertions.assertEquals(2, branches.size());
            Assertions.assertEquals(1,branches.get(0).getBraId());
            Assertions.assertEquals(2,branches.get(1).getBraId());

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Kiểm tra lấy thông tin chi nhánh khi từ khóa truyền vào là 1 chuỗi rỗng
    // Phải trả về tổng tất cả các chi nhánh (2 chi nhánh có mã là 1,2)
    @Test
    public void testSelectAllBranchByEmptyKw() {
        try {
            List<Branch> branches = branchService.getBranches("");
            Assertions.assertEquals(2, branches.size());
            Assertions.assertEquals(1, branches.get(0).getBraId());
            Assertions.assertEquals(2, branches.get(1).getBraId());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Kiểm tra lấy thông tin chi nhánh khi từ khóa truyền vào là 1 chuỗi khoảng trắng
    // Phải trả về 2
    @Test
    public void testSelectAllBranchBySpacesKw() {
        try {
            List<Branch> branches = branchService.getBranches("       ");
            Assertions.assertEquals(2, branches.size());
            Assertions.assertEquals(1, branches.get(0).getBraId());
            Assertions.assertEquals(2, branches.get(1).getBraId());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Kiểm tra lấy thông tin chi nhánh khi từ khóa truyền vào là tên 1 chi nhánh dưới database
    // Có 1 chi nhánh tên "Tên chi nhánh thứ 2"
    @Test
    public void testSelectAllBranchByValidKw() {
        try {
            List<Branch> branches = branchService.getBranches("Tên chi nhánh thứ 2");
            Assertions.assertEquals(1, branches.size());
            Assertions.assertEquals(2, branches.get(0).getBraId());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Kiểm tra lấy thông tin chi nhánh khi truyền vào là 1 tên chi nhánh không tồn tại dưới database
    // Không có chi nhánh nào tên "Tên chi nhánh thứ 9999999999"
    @Test
    public void testSelectAllBranchByInValid() {
        try {
            List<Branch> branches = branchService.getBranches("Tên chi nhánh thứ 9999999999");
            Assertions.assertEquals(0, branches.size());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // Kiểm tra số lấy số lượng chi nhánh còn hoạt động dưới database
    // Có 2 chi nhánh dưới database nhưng chỉ có 1 chi nhánh còn hoạt động
    @Test
    public void testGetBranchAmount() {
        try {
            Branch branch = branchServiceForTest.getBranchById(1);
            branchService.deleteBranch(branch);
            int amount = branchService.getBranchAmount();
            Assertions.assertEquals(1, amount);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Kiểm tra thêm giá trị null khi thêm chi nhánh
    // Trả về false
    @Test
    public void testAddBranchWithNull() {
        try {
            Assertions.assertFalse(branchService.addBranch(null));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Kiểm tra thêm thông tin chi nhánh khi thông tin không hợp lệ
    //  Trả về false
    @Test
    public void testAddBranchWithInvalidInformation() {
        try {
            Branch branch = new Branch();
            branch.setBraName("");
            branch.setBraAddress("");
            Assertions.assertFalse(branchService.addBranch(branch));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Kiểm tra thêm thông tin chi nhánh đã tồn tại
    // Chi nhánh có mã là 2 đã tồn tại. Trả về false
    @Test
    public void testAddBranchWithExist() {
        try {
            Branch branch = branchServiceForTest.getBranchById(2);
            Assertions.assertFalse(branchService.addBranch(branch));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Kiểm tra thêm chi nhánh mới thành công
    // Trả về true
    @Test
    public void testAddBranchWithValidInformation() {
        try {
            Branch branch = new Branch();
            branch.setBraName("Tên chi nhánh thứ 3");
            branch.setBraAddress("Địa chỉ chi nhánh thứ 3");
            int preAmo = branchService.getBranchAmount();
            Assertions.assertTrue(branchService.addBranch(branch));
            int nextAmo = branchService.getBranchAmount();
            Assertions.assertNotEquals(preAmo, nextAmo);
            Branch branchTest = branchServiceForTest.getBranchByBraName(branch.getBraName());
            Assertions.assertEquals( branch.getBraName(), branchTest.getBraName());
            Assertions.assertEquals( branch.getBraAddress(), branchTest.getBraAddress());
            Assertions.assertTrue(branchTest.getBraIsActive());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // Kiểm tra thêm giá trị null khi sửa chi nhánh
    // Trả về false
    @Test
    public void testUpdateBranchWithNull() {
        try {
            Assertions.assertFalse(branchService.updateBranch(null));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Kiểm tra sửa thông tin chi nhánh khi thông tin không hợp lệ
    //  Trả về false
    @Test
    public void testUpdateBranchWithInvalidInformation() {
        try {
            Branch branch = branchServiceForTest.getBranchById(1);
            branch.setBraName("");
            branch.setBraAddress("");
            Assertions.assertFalse(branchService.updateBranch(branch));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Kiểm tra sửa thông tin chi nhánh đã tồn tại mà thông tin vừa sửa
    // trùng với thông tin chi nhánh khác đã tồn tại
    // Sửa thông tin chi nhánh 1 trùng với thông tin chi nhánh 2 mà
    // chi nhánh có mã là 2 đã tồn tại. Trả về false
    @Test
    public void testUpdateBranchWithExist() {
        try {
            Branch branch = branchServiceForTest.getBranchById(1);
            branch.setBraName("Tên chi nhánh thứ 2");
            branch.setBraAddress("Địa chỉ chi nhánh thứ 2");
            Assertions.assertFalse(branchService.updateBranch(branch));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Kiểm tra sửa chi nhánh mới thành công
    // Trả về true
    @Test
    public void testUpdateBranchWithValidInformation() {
        try {
            Branch branch = branchServiceForTest.getBranchById(1);
            branch.setBraName("Tên chi nhánh thứ 3");
            branch.setBraAddress("Địa chỉ chi nhánh thứ 3");
            Assertions.assertTrue(branchService.updateBranch(branch));
            Branch branchTest = branchServiceForTest.getBranchByBraName(branch.getBraName());
            Assertions.assertEquals( branch.getBraName(), branchTest.getBraName());
            Assertions.assertEquals( branch.getBraAddress(), branchTest.getBraAddress());
            Assertions.assertTrue(branchTest.getBraIsActive());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Kiểm tra giá trị null khi xóa chi nhánh
    // Trả về false
    @Test
    public void testDeleteBranchWithNull() {
        try {
            Assertions.assertFalse(branchService.deleteBranch(null));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Kiểm tra xóa thông tin chi nhánh khi thông tin không hợp lệ
    //  Trả về false
    @Test
    public void testDeleteBranchWithInvalidInformation() {
        try {
            Branch branch = branchServiceForTest.getBranchById(1);
            branch.setBraId(null);
            Assertions.assertFalse(branchService.deleteBranch(branch));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Kiểm tra xóa thông tin chi nhánh không tồn tại
    // chi nhánh có mã là 9999 không tồn tại. Trả về false
    @Test
    public void testDeleteBranchWithExist() {
        try {
            Branch branch = branchServiceForTest.getBranchById(1);
            branch.setBraId(9999);
            Assertions.assertFalse(branchService.deleteBranch(branch));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Kiểm tra xóa chi nhánh thành công
    // Trả về true
    @Test
    public void testDeleteBranchWithValidInformation() {
        try {
            Branch branch = branchServiceForTest.getBranchById(1);
            int preAmo = branchService.getBranchAmount();
            Assertions.assertTrue(branchService.deleteBranch(branch));
            int nextAmo = branchService.getBranchAmount();
            Assertions.assertNotEquals(preAmo, nextAmo);
            Branch branchTest = branchServiceForTest.getBranchByBraName(branch.getBraName());
            Assertions.assertFalse(branchTest.getBraIsActive());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
