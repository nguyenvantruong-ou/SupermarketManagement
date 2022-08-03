package com.ou.repositories;

import com.ou.pojos.Member;
import com.ou.pojos.MemberType;
import com.ou.utils.DatabaseUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MemberRepositoryForTest {
    // Lấy thông tin  thành viên dựa vào id
    public Member getMemberById(int memId) throws SQLException {
        try (Connection connection = DatabaseUtils.getConnection()) {
            String query = "SELECT * FROM Person p, Member m LEFT JOIN MemberType mt ON m.memt_id = mt.memt_id " +
                    "WHERE p.pers_id = m.mem_id AND pers_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, memId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                Member member = new Member();
                member.setPersId(resultSet.getInt("pers_id"));
                member.setPersSex(resultSet.getByte("pers_sex"));
                member.setPersLastName(resultSet.getString("pers_last_name"));
                member.setPersFirstName(resultSet.getString("pers_first_name"));
                member.setPersDateOfBirth(resultSet.getDate("pers_date_of_birth"));
                member.setPersJoinedDate(resultSet.getDate("pers_joined_date"));
                member.setPersPhoneNumber(resultSet.getString("pers_phone_number"));
                member.setPersIdCard(resultSet.getString("pers_id_card"));
                member.setMemTotalPurchase(resultSet.getBigDecimal("mem_total_purchase"));
                member.setPersIsActive(resultSet.getBoolean("pers_is_active"));
                MemberType memberType = new MemberType();
                memberType.setMemtIsActive(resultSet.getBoolean("memt_is_active"));
                memberType.setMemtId(resultSet.getInt("memt_id"));
                memberType.setMemtName(resultSet.getString("memt_name"));
                member.setMemberType(memberType);
                return member;
            }
            return null;
        }
    }

    // Lấy thông tin  thành viên dựa vào cccd/cmnd
    public Member getMemberByIdCard(String idCard) throws SQLException {
        try (Connection connection = DatabaseUtils.getConnection()) {
            String query = "SELECT * FROM Person p, Member m LEFT JOIN MemberType mt ON m.memt_id = mt.memt_id " +
                    "WHERE p.pers_id = m.mem_id AND pers_id_card = ? AND pers_is_active = TRUE";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, idCard);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                Member member = new Member();
                member.setPersId(resultSet.getInt("pers_id"));
                member.setPersSex(resultSet.getByte("pers_sex"));
                member.setPersLastName(resultSet.getString("pers_last_name"));
                member.setPersFirstName(resultSet.getString("pers_first_name"));
                member.setPersDateOfBirth(resultSet.getDate("pers_date_of_birth"));
                member.setPersJoinedDate(resultSet.getDate("pers_joined_date"));
                member.setPersPhoneNumber(resultSet.getString("pers_phone_number"));
                member.setPersIdCard(resultSet.getString("pers_id_card"));
                member.setMemTotalPurchase(resultSet.getBigDecimal("mem_total_purchase"));
                MemberType memberType = new MemberType();
                memberType.setMemtIsActive(resultSet.getBoolean("memt_is_active"));
                memberType.setMemtId(resultSet.getInt("memt_id"));
                memberType.setMemtName(resultSet.getString("memt_name"));
                member.setMemberType(memberType);
                return member;
            }
            return null;
        }
    }

    // lấy danh sách thành viên còn hoạt động
    public List<Member> getMembers(String kw) throws SQLException {
        List<Member> members = new ArrayList<>();
        try (Connection connection = DatabaseUtils.getConnection()) {
            String query = "SELECT * FROM Person p, Member m LEFT JOIN MemberType mt ON m.memt_id = mt.memt_id\n" +
                    "WHERE p.pers_id = m.mem_id AND (pers_first_name LIKE CONCAT(\"%\", ? , \"%\") OR " +
                    "pers_last_name LIKE CONCAT(\"%\", ? , \"%\")) AND p.pers_is_active = TRUE";
            if (kw == null)
                kw = "";
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

}
