package com.ou.services;

import com.ou.pojos.Member;
import com.ou.utils.DatabaseUtils;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class MemberServiceTest {
    private static Connection connection;
    private static MemberService memberService;
    private static MemberServiceForTest memberServiceForTest;
    public MemberServiceTest() {

    }

    @BeforeAll
    public static void setUpClass() {
        try {
            connection = DatabaseUtils.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        memberServiceForTest = new MemberServiceForTest();
        memberService= new MemberService();
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

//    (có 4 member còn hoạt động dưới db có id là 5,6,7,8)
    private List<Integer> getMemberIds(){
        List<Integer> memberIds = new ArrayList<>();
        memberIds.add(5);
        memberIds.add(6);
        memberIds.add(7);
        memberIds.add(8);
        return memberIds;
    }

    // kiểm tra lấy số lượng thành viên
    // trả về 4, (có 4 member còn hoạt động dưới db có id là 5,6,7,8)
    @Test
    public void testGetTotalAmountMember(){
        try {
            int amount = memberService.getMemberAmount();
            Assertions.assertEquals(4, amount);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    // Kiểm tra lấy thông tin thành viên khi từ khóa truyền vào là null
    // Phải trả về tất cả các thành viên đang còn hoạt động
    @Test
    public void testSelectAllMemberByNullKw() {
        try {
            List<Member> members = memberService.getMembers(null);
            int amount = memberService.getMemberAmount();
            Assertions.assertEquals(amount, members.size());

            List<Integer> memberIds = getMemberIds();
            for(int i=0; i<members.size(); i++)
                Assertions.assertEquals(memberIds.get(i), members.get(i).getPersId());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Kiểm tra lấy thông tin thành viên khi từ khóa truyền vào là chuỗi rỗng
    // Phải trả về tất cả các thành viên đang còn hoạt động
    @Test
    public void testSelectAllMemberByEmptyKw() {
        try {
            List<Member> members = memberService.getMembers("");
            int amount = memberService.getMemberAmount();
            Assertions.assertEquals(amount, members.size());

            List<Integer> memberIds = getMemberIds();
            for(int i=0; i<members.size(); i++)
                Assertions.assertEquals(memberIds.get(i), members.get(i).getPersId());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Kiểm tra lấy thông tin thành viên có họ là "Trần Anh"
    // Phải trả về 1 thành viên
    @Test
    public void testSelectAllMemberByValidKw() {
        try {
            List<Member> members = memberService.getMembers("Trần Anh");
            Assertions.assertEquals(1, members.size());
            Assertions.assertEquals("Trần Anh", members.get(0).getPersLastName());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Kiểm tra lấy thông tin thành viên có tên là "Long"
    // Phải trả về 1 thành viên
    @Test
    public void testSelectAllMemberByValidKw2() {
        try {
            List<Member> members = memberService.getMembers("Long");
            Assertions.assertEquals(1, members.size());
            Assertions.assertEquals("Long", members.get(0).getPersFirstName());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Kiểm tra lấy thông tin thành viên khi từ khóa truyền vào không tồn tại dưới DATABASE
    // Không có thành viên nào tên "Nguyễn Thành Danh"
    @Test
    public void testSelectAllMemberByInValidKw() {
        try {
            List<Member> members = memberService.getMembers("Nguyễn Thành Danh");
            Assertions.assertEquals(0, members.size());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // Kiểm tra thêm giá trị null khi thêm thành viên
    // Trả về false
    @Test
    public void testAddMemberWithNull(){
        try {
            int preAmount = memberService.getMemberAmount();
            Assertions.assertFalse(memberService.addMember(null));

            int nextAmount = memberService.getMemberAmount();
            Assertions.assertEquals(preAmount, nextAmount);
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    // Kiểm tra thêm thông tin thành viên khi thông tin không hợp lệ
    //  Trả về false
    @Test
    public void testAddMemberWithInvalidInformation(){
        try {
            int preAmount = memberService.getMemberAmount();
            Member member = new Member();
            member.setPersFirstName("");
            member.setPersLastName("");
            member.setPersIdCard("");
            Assertions.assertFalse(memberService.addMember(member));

            int nextAmount = memberService.getMemberAmount();
            Assertions.assertEquals(preAmount, nextAmount);
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    // Kiểm tra thêm thông tin thành viên đã tồn tại
    // thêm mới thành viên bằng thông tin của thành viên có mã là 6. Trả về false
    @Test
    public void testAddMemberWithExist(){
        try {
            int preAmount = memberService.getMemberAmount();
            Member member = memberServiceForTest.getMemberById(6);
            Assertions.assertFalse(memberService.addMember(member));

            int nextAmount = memberService.getMemberAmount();
            Assertions.assertEquals(preAmount, nextAmount);
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    // Kiểm tra thêm thành viên mới thành công
    // Trả về true
    @Test
    public void testAddMemberWithValidInfomation(){
        try {
            Member member = new Member();
            member.setPersLastName("Nguyen Van");
            member.setPersFirstName("A");
            member.setPersIdCard("352430926");
            member.setPersPhoneNumber("0567255612");
            member.setPersIsActive(true);
            member.setPersDateOfBirth(Date.valueOf("2001-04-16"));
            member.setPersSex((byte) 1);
            int preAmo = memberService.getMemberAmount();
            Assertions.assertTrue(memberService.addMember(member));

            int nextAmo = memberService.getMemberAmount();
            Assertions.assertNotEquals(preAmo, nextAmo);

            Member memberAdd = memberServiceForTest.getMemberByIdCard("352430926");
            Assertions.assertEquals(memberAdd.getPersLastName(), member.getPersLastName());
            Assertions.assertEquals(memberAdd.getPersFirstName(), member.getPersFirstName());
            Assertions.assertEquals(memberAdd.getPersPhoneNumber(), member.getPersPhoneNumber());
            Assertions.assertEquals(memberAdd.getPersDateOfBirth(), member.getPersDateOfBirth());
            Assertions.assertEquals(memberAdd.getPersSex(), member.getPersSex());
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    // Kiểm tra thêm giá trị null khi sửa thành viên
    // Trả về false
    @Test
    public void testUpdateMemberWithNull(){
        try {
            Assertions.assertFalse(memberService.updateMember(null));
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    // Kiểm tra sửa thông tin thành viên khi thông tin không hợp lệ
    //  Trả về false
    @Test
    public void testUpdateMemberWithInvalidInformation(){
        try {
            Member member = memberServiceForTest.getMemberById(6);
            member.setPersPhoneNumber("");
            member.setPersIdCard("");
            Assertions.assertFalse(memberService.updateMember(member));

            Member memberUpdate = memberServiceForTest.getMemberById(6);
            Assertions.assertNotEquals(memberUpdate.getPersPhoneNumber(), member.getPersPhoneNumber());
            Assertions.assertNotEquals(memberUpdate.getPersIdCard(), member.getPersIdCard());
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    // Kiểm tra sửa thông tin thành viên  mà thông tin vừa sửa trùng với thông tin thành viên khác
    // Sửa thông tin thành viên 6 trùng với thông tin thành viên 5. Trả về false
    @Test
    public void testUpdateMemberWithExist(){
        try {
            Member member5 = memberServiceForTest.getMemberById(5);
            Member member6 = memberServiceForTest.getMemberById(6);
            member6.setPersIdCard(member5.getPersIdCard());
            Assertions.assertFalse(memberService.updateMember(member6));

            Member member6Update = memberServiceForTest.getMemberById(6);
            Assertions.assertNotEquals(member6Update.getPersIdCard(), member6.getPersIdCard());

        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    // Kiểm tra sửa thành viên thành công
    // Trả về true
    @Test
    public void testUpdateMemberWithValidInfomation(){
        try {
            Member member = memberServiceForTest.getMemberById(7);
            member.setPersFirstName("Danh");
            Assertions.assertTrue(memberService.updateMember(member));

            Member memberUpdate = memberServiceForTest.getMemberById(7);
            Assertions.assertEquals("Danh", memberUpdate.getPersFirstName());
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    // Kiểm tra giá trị null khi xóa thành viên
    // Trả về false
    @Test
    public void testDeleteMemberWithNull(){
        try {
            Assertions.assertFalse(memberService.deleteMember(null));
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    // Kiểm tra xóa thông tin thành viên không tồn tại
    @Test
    public void testDeleteMemberWithExist(){
        try {
            Member member = memberServiceForTest.getMemberById(5);
            member.setPersId(9999);
            Assertions.assertFalse(memberService.deleteMember(member));
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    // Kiểm tra xóa thành viên thành công
    // Trả về true
    @Test
    public void testDeleteMemberWithValidInformation(){
        try {
            Member member = memberServiceForTest.getMemberById(8);
            Assertions.assertTrue(memberService.deleteMember(member));

            Member memberUpdate = memberServiceForTest.getMemberById(8);
            Assertions.assertFalse(memberUpdate.getPersIsActive());
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    // kiểm tra lấy thông tin member khi truyền vào null id
    // trả ra null object
    @Test
    public void testGetMemberByIdNull(){
        try {
            Member member = memberService.getMemberById(null);
            Assertions.assertNull(member);
        }catch (SQLException e){
            e.printStackTrace();
        }
    }
    // kiểm tra lấy thông tin member khi truyền vào id không tồn tại
    // trả ra null object
    @Test
    public void testGetMemberByIdNotExist(){
        try {
            Member member = memberService.getMemberById(9999999);
            Assertions.assertNull(member);
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    // kiểm tra lấy thông tin member khi truyền vào id tồn tại
    // trả ra limit sale object
    @Test
    public void testGetMemberByIdExist(){
        try {
            Member member = memberService.getMemberById(5);
            Assertions.assertNotNull(member);
        }catch (SQLException e){
            e.printStackTrace();
        }
    }
}
