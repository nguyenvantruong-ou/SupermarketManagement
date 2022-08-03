package com.ou.services;

import com.ou.pojos.MemberType;
import com.ou.pojos.Sale;
import com.ou.utils.DatabaseUtils;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MemberTypeServiceTest {
    private static Connection connection;
    private static MemberTypeService memberTypeService;
    private static MemberTypeServiceForTest memberTypeServiceForTest;
    public MemberTypeServiceTest() {

    }

    @BeforeAll
    public static void setUpClass() {
        try {
            connection = DatabaseUtils.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        memberTypeService = new MemberTypeService();
        memberTypeServiceForTest = new MemberTypeServiceForTest();
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

    // Dưới database có 5 member type 1,2,3,4,5
    private List<Integer> getMemberTypeIds(){
        List<Integer> memberTypeIds = new ArrayList<>();
        for(int i=1; i<=5; i++)
            memberTypeIds.add(i);
        return memberTypeIds;
    }

    // kiểm tra lấy số lượng member type
    // trả về 5 (vì có 5 membertype dưới db)
    @Test
    public void testGetTotalAmountMemberType() {
        try {
            int amount = memberTypeService.getTotalAmountMemberType();
            Assertions.assertEquals(5, amount);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // kiểm tra lấy danh sách member type khi truyền vào ""
    // trả về tất cả member type
    @Test
    public void testSelectAllMemberTypeByEmptyKw() {
        try {
            List<MemberType> memberTypes = memberTypeService.getMemberTypes("");
            int amount = memberTypeService.getTotalAmountMemberType();
            Assertions.assertEquals(amount, memberTypes.size());

            List<Integer> memberTypeIds = getMemberTypeIds();
            for(int i=0; i<memberTypes.size(); i++)
                Assertions.assertEquals(memberTypeIds.get(i), memberTypes.get(i).getMemtId());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // kiểm tra lấy danh sách member type khi truyền vào kw hợp lệ
    // có 1 member type tên là: "Thành viên bạc" dưới db
    // trả về 1 thành viên có tên là "Thành viên bạc"
    @Test
    public void testSelectAllMemberTypeByValidKw() {
        try {
            List<MemberType> memberTypes = memberTypeService.getMemberTypes("Thành viên bạc");
            Assertions.assertEquals(1, memberTypes.size());
            Assertions.assertEquals("Thành viên bạc", memberTypes.get(0).getMemtName());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // kiểm tra thêm mới một member type với giá trị memt_total_money không hợp lệ
    // trả về false
    @Test
    public void testAddMemberTypeWithInValidTotalMoney() {
        try {
            MemberType memberType = new MemberType();
            memberType.setMemtName("test add member type");
            Sale s = new Sale();
            s.setSaleId(1);
            memberType.setSale(s);
            memberType.setMemtTotalMoney(new BigDecimal(-1));
            Assertions.assertFalse(memberTypeService.addMemberType(memberType));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // kiểm tra thêm mới một member type với memt_name không hợp lệ
    // trả về false
    @Test
    public void testAddMemberTypeWithMemberTypeNull() {
        try {
            MemberType memberType = new MemberType();
            memberType.setMemtName("");
            Assertions.assertFalse(memberTypeService.addMemberType(memberType));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    // kiểm tra thêm mới member type với thông tin hợp lệ
    // trả về true
    @Test
    public void testAddMemberTypeWithInfoValid() {
        try {
            MemberType memberType = new MemberType();
            memberType.setMemtName("test add member type");
            Sale s = new Sale();
            s.setSaleId(1);
            memberType.setSale(s);
            memberType.setMemtTotalMoney(new BigDecimal(150000));
            Assertions.assertTrue(memberTypeService.addMemberType(memberType));

            MemberType memberTypeNew = memberTypeServiceForTest.getMemberTypeByName("test add member type");
            Assertions.assertEquals(memberType.getMemtName(), memberTypeNew.getMemtName());
            Assertions.assertEquals(memberType.getMemtTotalMoney(), memberTypeNew.getMemtTotalMoney());
            Assertions.assertEquals(memberType.getSale().getSaleId(), memberTypeNew.getSale().getSaleId());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // kiểm tra sửa một member type với giá trị memt_total_money không hợp lệ
    // trả về false
    @Test
    public void testUpdateMemberTypeWithTotalMoneyInValid() {
        try {
            MemberType memberType = memberTypeServiceForTest.getMemberTypeById(1);
            memberType.setMemtTotalMoney(new BigDecimal(-1));
            Assertions.assertFalse(memberTypeService.updateMemberType(memberType));

            MemberType memberTypeUpdate = memberTypeServiceForTest.getMemberTypeById(1);
            Assertions.assertNotEquals(new BigDecimal(-1), memberTypeUpdate.getMemtTotalMoney());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // kiểm tra sửa một member type với member type name không hợp lệ
    // trả về false
    @Test
    public void testUpdateMemberTypeWithInValidInfo() {
        try {
            MemberType memberType = memberTypeServiceForTest.getMemberTypeById(1);
            memberType.setMemtName("");
            Assertions.assertFalse(memberTypeService.updateMemberType(memberType));

            MemberType memberTypeUpdate = memberTypeServiceForTest.getMemberTypeById(1);
            Assertions.assertNotEquals(memberType.getMemtName(), memberTypeUpdate.getMemtName());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // kiểm tra sửa với member name đã tồn tại dưới db
    // trả về false
    @Test
    public void testUpdateMemberTypeWithNameIsExist() {
        try {
            MemberType memberType = memberTypeServiceForTest.getMemberTypeById(1);
            memberType.setMemtName("Thành viên bạc");
            Assertions.assertFalse(memberTypeService.updateMemberType(memberType));

            MemberType memberTypeUpdate = memberTypeServiceForTest.getMemberTypeById(1);
            Assertions.assertNotEquals(memberType.getMemtName(), memberTypeUpdate.getMemtName());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // kiểm tra sửa member type với thông tin hợp lệ
    // trả về true
    @Test
    public void testUpdateMemberTypeWithValidInfo() {
        try {
            MemberType memberType = memberTypeServiceForTest.getMemberTypeById(1);
            memberType.setMemtName("Thành viên Test");
            Assertions.assertTrue(memberTypeService.updateMemberType(memberType));

            MemberType memberTypeUpdate = memberTypeServiceForTest.getMemberTypeById(1);
            Assertions.assertEquals(memberType.getMemtName(), memberTypeUpdate.getMemtName());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // kiểm tra xóa một member type với member type không hợp lệ
    // trả về false
    @Test
    public void testDeleteMemberTypeWithInValid() {
        try {
            MemberType memberType = new MemberType();
            memberType.setMemtId(0);
            Assertions.assertFalse(memberTypeService.deleteMemberType(memberType));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // kiểm tra xóa member type hợp lệ
    // trả về true khi xoá
    // trả về false khi lấy trường active
    @Test
    public void testDeleteMemberTypeWithValid() {
        try {
            MemberType memberType = new MemberType();
            memberType.setMemtId(1);
            Assertions.assertTrue(memberTypeService.deleteMemberType(memberType));

            MemberType memberTypeDel = memberTypeServiceForTest.getMemberTypeById(1);
            Assertions.assertFalse(memberTypeDel.getMemtIsActive());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
