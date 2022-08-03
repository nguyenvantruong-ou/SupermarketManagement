package com.ou.services;

import com.ou.pojos.Member;
import com.ou.pojos.SalePercent;
import com.ou.repositories.MemberRepository;

import java.sql.SQLException;
import java.util.List;

public class MemberService {
    private static final MemberRepository MEMBER_REPOSITORY;

    static {
        MEMBER_REPOSITORY = new MemberRepository();
    }

    //lấy danh sách thành viên
    public List<Member> getMembers(String kw) throws SQLException {
        return MEMBER_REPOSITORY.getMembers(kw);
    }

    //lấy tổng số lượng thành viên
    public int getMemberAmount() throws SQLException {
        return MEMBER_REPOSITORY.getMemberAmount();
    }


    //thêm mới một thành viên
    public boolean addMember(Member member) throws SQLException {
        if (member == null || member.getPersFirstName().isEmpty() || member.getPersLastName().isEmpty() ||
                member.getPersFirstName() == null || member.getPersLastName() == null ||
                member.getPersDateOfBirth() == null || member.getPersSex() == null || member.getPersIdCard() == null ||
                member.getPersPhoneNumber() == null)
            return false;
        if (MEMBER_REPOSITORY.isExistPhoneNumber(member.getPersPhoneNumber()) &&
                MEMBER_REPOSITORY.isExistIdCard(member.getPersIdCard())) {
            Member memberAdd = MEMBER_REPOSITORY.getMemberByPhone(member.getPersPhoneNumber());
            if (!memberAdd.getPersIsActive())
                return MEMBER_REPOSITORY.addMember(memberAdd);
            return false;
        }
        if (MEMBER_REPOSITORY.isExistIdCard(member.getPersIdCard()) ||
                MEMBER_REPOSITORY.isExistPhoneNumber(member.getPersPhoneNumber()))
            return false;
        return MEMBER_REPOSITORY.addMember(member);
    }

    //cập nhật thông tin cho một thành viên
    public boolean updateMember(Member member) throws SQLException {
        if (member == null || member.getPersFirstName().isEmpty() || member.getPersLastName().isEmpty() ||
                member.getPersFirstName() == null || member.getPersLastName() == null ||
                member.getPersDateOfBirth() == null || member.getPersSex() == null || member.getPersIdCard() == null ||
                member.getPersPhoneNumber() == null || member.getPersPhoneNumber().isEmpty() ||
                member.getPersId() == null || member.getPersIdCard().isEmpty())
            return false;
        if (MEMBER_REPOSITORY.isExistPhoneNumber(member.getPersPhoneNumber(), member.getPersId()) ||
                MEMBER_REPOSITORY.isExistIdCard(member.getPersIdCard(), member.getPersId()))
            return false;
        return MEMBER_REPOSITORY.updateMember(member);
    }

    //xóa một thành viên
    public boolean deleteMember(Member member) throws SQLException {
        if (member == null || member.getPersId() == null)
            return false;
        return MEMBER_REPOSITORY.deleteMember(member);
    }

    //Lấy thông tin của người
    public Member getMemberById(Integer memId) throws SQLException {
        if (memId == null)
            return null;
        return MEMBER_REPOSITORY.getMemberById(memId);
    }

    // update member type id for member
    public void updateMemberTypeId(Member member, int memberTypeId) throws SQLException {
        MEMBER_REPOSITORY.updateMemberTypeId(member, memberTypeId);
    }


    // Lấy thông tin giảm giá của loại thành viên
    public SalePercent getSalePercentOfMember(int memId ) throws SQLException {
        return MEMBER_REPOSITORY.getSalePercentOfMember(memId);
    }
}
