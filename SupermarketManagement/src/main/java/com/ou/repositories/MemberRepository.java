package com.ou.repositories;

import com.ou.pojos.Member;
import com.ou.pojos.MemberType;
import com.ou.pojos.SalePercent;
import com.ou.utils.DatabaseUtils;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MemberRepository {
    //lấy danh sách thành viên
    public List<Member> getMembers(String kw) throws SQLException {
        List<Member> members = new ArrayList<>();
        try (Connection connection = DatabaseUtils.getConnection()) {
            String query = "SELECT * FROM Person p, Member m LEFT JOIN MemberType mt ON m.memt_id = mt.memt_id\n" +
                    "WHERE p.pers_id = m.mem_id AND (pers_first_name LIKE CONCAT(\"%\", ? , \"%\") OR " +
                    "pers_last_name LIKE CONCAT(\"%\", ? , \"%\"))";
            if (kw == null)
                kw = "";
            else kw=kw.trim();
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, kw);
            preparedStatement.setString(2, kw);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Member member = new Member();
                member.setPersId(resultSet.getInt("pers_id"));
                member.setPersFirstName(resultSet.getString("pers_first_name"));
                member.setPersLastName(resultSet.getString("pers_last_name"));
                member.setPersPhoneNumber(resultSet.getString("pers_phone_number"));
                member.setPersIdCard(resultSet.getString("pers_id_card"));
                member.setPersSex(resultSet.getByte("pers_sex"));
                member.setPersDateOfBirth(resultSet.getDate("pers_date_of_birth"));
                member.setPersJoinedDate(resultSet.getDate("pers_joined_date"));
                member.setPersIsActive(resultSet.getBoolean("pers_is_active"));
                member.setMemTotalPurchase(resultSet.getBigDecimal("mem_total_purchase"));
                MemberType memberType = new MemberType();
                memberType.setMemtId(resultSet.getInt("pers_id"));
                memberType.setMemtName(resultSet.getString("memt_name"));
                memberType.setMemtTotalMoney(resultSet.getBigDecimal("memt_total_money"));
                memberType.setMemtIsActive(resultSet.getBoolean("memt_is_active"));
                member.setMemberType(memberType);
                members.add(member);
            }
        }
        return members;
    }

    //lấy tổng số lượng thành viên
    public int getMemberAmount() throws SQLException {
        try (Connection connection = DatabaseUtils.getConnection()) {
            String query = "SELECT count(p.pers_id) as mem_amount " +
                    "FROM Person p, Member m LEFT JOIN MemberType mt ON m.memt_id = mt.memt_id " +
                    "WHERE p.pers_id = m.mem_id AND p.pers_is_active = TRUE";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next())
                return resultSet.getInt("mem_amount");
            return 0;
        }
    }

    //thêm mới một thành viên
    public boolean addMember(Member member) throws SQLException {
        try (Connection connection = DatabaseUtils.getConnection()) {
            if (!member.getPersIsActive()) {
                String query = "UPDATE Person SET pers_is_active = TRUE where pers_id = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setInt(1, member.getPersId());
                return 1 == preparedStatement.executeUpdate();
            }
            String query = "INSERT INTO Person (pers_id_card, pers_phone_number, pers_sex, pers_last_name, pers_first_name, " +
                    "pers_date_of_birth, pers_is_active) VALUES (?,?,?,?,?,?,?)";
            PreparedStatement preparedStatement1 = connection.prepareStatement(query);
            preparedStatement1.setString(1, member.getPersIdCard());
            preparedStatement1.setString(2, member.getPersPhoneNumber());
            preparedStatement1.setByte(3, member.getPersSex());
            preparedStatement1.setString(4, member.getPersLastName());
            preparedStatement1.setString(5, member.getPersFirstName());
            preparedStatement1.setDate(6, (Date) member.getPersDateOfBirth());
            preparedStatement1.setBoolean(7, member.getPersIsActive());
            preparedStatement1.executeUpdate();
            String query2 = "INSERT INTO Member (mem_id, mem_total_purchase) VALUES (?,?)";
            PreparedStatement preparedStatement2 = connection.prepareStatement(query2);
            preparedStatement2.setInt(1, getPersIdByPhone(member.getPersPhoneNumber()));
            preparedStatement2.setBigDecimal(2, new BigDecimal(0));
            return 1 == preparedStatement2.executeUpdate();
        }
    }

    //cập nhật thông tin cho một thành viên
    public boolean updateMember(Member member) throws SQLException {
        try (Connection connection = DatabaseUtils.getConnection()) {
            String query = "UPDATE person SET pers_first_name = ?, pers_last_name = ?, pers_sex = ?, pers_date_of_birth = ?," +
                    "pers_id_card = ?, pers_phone_number = ? WHERE pers_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, member.getPersFirstName());
            preparedStatement.setString(2, member.getPersLastName());
            preparedStatement.setByte(3, member.getPersSex());
            preparedStatement.setDate(4, (Date) member.getPersDateOfBirth());
            preparedStatement.setString(5, member.getPersIdCard());
            preparedStatement.setString(6, member.getPersPhoneNumber());
            preparedStatement.setInt(7, member.getPersId());
            return 1 == preparedStatement.executeUpdate();
        }
    }

    //xóa một thành viên
    public boolean deleteMember(Member member) throws SQLException {
        try (Connection connection = DatabaseUtils.getConnection()) {
            String query = "UPDATE person SET pers_is_active = 0 WHERE pers_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, member.getPersId());
            return 1 == preparedStatement.executeUpdate();
        }
    }

    // lấy id dựa vào số điện thoại
    private int getPersIdByPhone(String phone) throws SQLException {
        try (Connection connection = DatabaseUtils.getConnection()) {
            String query = "SELECT pers_id FROM Person WHERE pers_phone_number = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, phone);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next())
                return resultSet.getInt("pers_id");
            return 0;
        }
    }

    // lấy member dựa vào số điện thoại
    public Member getMemberByPhone(String phone) throws SQLException {
        try (Connection connection = DatabaseUtils.getConnection()) {
            String query = "SELECT * FROM Person WHERE pers_phone_number = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, phone);
            ResultSet resultSet = preparedStatement.executeQuery();
            Member member = new Member();
            if (resultSet.next()) {
                member.setPersId(resultSet.getInt("pers_id"));
                member.setPersFirstName(resultSet.getString("pers_first_name"));
                member.setPersLastName(resultSet.getString("pers_last_name"));
                member.setPersPhoneNumber(resultSet.getString("pers_phone_number"));
                member.setPersIdCard(resultSet.getString("pers_id_card"));
                member.setPersSex(resultSet.getByte("pers_sex"));
                member.setPersDateOfBirth(resultSet.getDate("pers_date_of_birth"));
                member.setPersJoinedDate(resultSet.getDate("pers_joined_date"));
                member.setPersIsActive(resultSet.getBoolean("pers_is_active"));
            }
            return member;
        }
    }

    // kiểm tra số tài khoản đã tồn tại? (for add-function)
    public boolean isExistIdCard(String id_card) throws SQLException {
        try (Connection connection = DatabaseUtils.getConnection()) {
            String query = "SELECT * FROM Person WHERE pers_id_card = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, id_card);
            return preparedStatement.executeQuery().next();
        }
    }

    // kiểm tra số tài khoản đã tồn tại? (for update-function)
    public boolean isExistIdCard(String id_card, int id) throws SQLException {
        try (Connection connection = DatabaseUtils.getConnection()) {
            String query = "SELECT * FROM Person WHERE pers_id_card = ? and pers_id != ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, id_card);
            preparedStatement.setInt(2, id);
            return preparedStatement.executeQuery().next();
        }
    }

    // kiểm tra số điện thoại đã tồn tại? (for add-function)
    public boolean isExistPhoneNumber(String phone_number) throws SQLException {
        try (Connection connection = DatabaseUtils.getConnection()) {
            String query = "SELECT * FROM Person WHERE pers_phone_number = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, phone_number);
            return preparedStatement.executeQuery().next();
        }
    }

    // kiểm tra số điện thoại đã tồn tại? (for update-function)
    public boolean isExistPhoneNumber(String phone_number, int id) throws SQLException {
        try (Connection connection = DatabaseUtils.getConnection()) {
            String query = "SELECT * FROM Person WHERE pers_phone_number = ? and pers_id != ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, phone_number);
            preparedStatement.setInt(2, id);
            return preparedStatement.executeQuery().next();
        }

    }

    //Lấy thông tin của người
    public Member getMemberById(Integer memId) throws SQLException {
        try (Connection connection = DatabaseUtils.getConnection()) {
            String query = "SELECT p.pers_first_name, p.pers_last_name, mt.memt_name, p.pers_date_of_birth, " +
                    "mt.memt_total_money, mt.memt_id " +
                    "FROM Member m JOIN Person p ON m.mem_id = p.pers_id " +
                    "JOIN MemberType mt ON m.memt_id = mt.memt_id " +
                    "WHERE m.mem_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, memId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                Member member = new Member();
                MemberType memberType = new MemberType();
                memberType.setMemtId(resultSet.getInt("memt_id"));
                memberType.setMemtName(resultSet.getString("memt_name"));
                memberType.setMemtTotalMoney(resultSet.getBigDecimal("memt_total_money"));
                member.setPersFirstName(resultSet.getString("pers_first_name"));
                member.setPersLastName(resultSet.getString("pers_last_name"));
                member.setPersDateOfBirth(resultSet.getDate("pers_date_of_birth"));
                member.setPersId(memId);
                member.setMemberType(memberType);
                return member;
            }
        }
        return null;
    }

    // update member type id for member
    public void updateMemberTypeId(Member member, int memberTypeId) throws SQLException {
        try (Connection connection = DatabaseUtils.getConnection()) {
            String query = "UPDATE Member SET memt_id = ?  WHERE mem_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            if (memberTypeId > 0)
                preparedStatement.setInt(1, memberTypeId);
            else
                preparedStatement.setString(1, null);
            preparedStatement.setInt(2, member.getPersId());
            preparedStatement.executeUpdate();
        }
    }

    // Lấy thông tin giảm giá của loại thành viên
    public SalePercent getSalePercentOfMember(int memId ) throws SQLException {
        try (Connection connection = DatabaseUtils.getConnection()){
            String query = "SELECT sper.sper_percent " +
                    "FROM Member mem JOIN MemberType memt ON mem.memt_id = memt.memt_id " +
                    "JOIN Sale s ON memt.sale_id = s.sale_id " +
                    "JOIN SalePercent sper ON s.sper_id = sper.sper_id " +
                    "WHERE mem.mem_id = ?";
            PreparedStatement preparedStatement  = connection.prepareStatement(query);
            preparedStatement.setInt(1, memId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()){
                SalePercent salePercent = new SalePercent();
                salePercent.setSperPercent(resultSet.getInt("sper_percent"));
                return salePercent;
            }
        }
        return null;
    }
}
