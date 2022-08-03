package com.ou.services;

import com.ou.pojos.MemberType;
import com.ou.repositories.MemberTypeRepository;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public class MemberTypeService {
    private final static MemberTypeRepository MEMBER_TYPE_REPOSITORY;
    static {
        MEMBER_TYPE_REPOSITORY = new MemberTypeRepository();
    }
    // lấy danh sách loại thành viên
    public List<MemberType> getMemberTypes(String kw) throws SQLException {
        return MEMBER_TYPE_REPOSITORY.getMemberTypes(kw);
    }
    // lấy danh sách loại thành viên
    public List<MemberType> getMemberTypes() throws SQLException {
        return MEMBER_TYPE_REPOSITORY.getMemberTypes();
    }

    // lấy tổng số member type còn hoạt động
    public int getTotalAmountMemberType() throws SQLException {
        return MEMBER_TYPE_REPOSITORY.getTotalAmountMemberType();
    }

    // lấy danh sách các mã giảm giá
    public List<String> getSaleIds() throws SQLException {
        return MEMBER_TYPE_REPOSITORY.getSaleIds();
    }

    // lấy sale percent by sale id
    public float getSalePercentBySaleId(int saleId) throws SQLException {
        return MEMBER_TYPE_REPOSITORY.getSalePercentBySaleId(saleId);
    }
    // thêm một loại thành viên
    public boolean addMemberType(MemberType memberType) throws SQLException {
        if(MEMBER_TYPE_REPOSITORY.isExitsMemberType(memberType.getMemtName()) && !memberType.getMemtIsActive())
            return MEMBER_TYPE_REPOSITORY.addMemberType(memberType);
        if(memberType.getMemtName().isEmpty() || memberType.getMemtName() == null
                || MEMBER_TYPE_REPOSITORY.isExitsMemberType(memberType.getMemtName()) || memberType.getMemtTotalMoney().equals(new BigDecimal(-1)))
            return false;
        return MEMBER_TYPE_REPOSITORY.addMemberType(memberType);
    }
    // sửa thông tin một loại thành viên
    public boolean updateMemberType(MemberType memberType) throws SQLException {
        if(memberType == null || memberType.getMemtId() == null || memberType.getMemtId() == 0 || memberType.getMemtName().isEmpty() || memberType.getMemtName() == null
                || memberType.getMemtTotalMoney().equals(new BigDecimal(-1)) || !memberType.getMemtIsActive()
                || MEMBER_TYPE_REPOSITORY.isExitsMemberType(memberType.getMemtName(), memberType.getMemtId()))
            return false;
        return MEMBER_TYPE_REPOSITORY.updateMemberType(memberType);
    }
    // xóa thông tin một loại thành viên
    public boolean deleteMemberType(MemberType memberType) throws SQLException {
        if(memberType == null || memberType.getMemtId() == null || memberType.getMemtId() == 0 || !memberType.getMemtIsActive())
            return false;
        return MEMBER_TYPE_REPOSITORY.deleteMemberType(memberType);
    }

    // kiểm tra tổng tiền thanh toán có trùng với loại thành viên khác không
    public boolean isExistTotalMoney(int memtId, BigDecimal totalMoney){
        return MEMBER_TYPE_REPOSITORY.isExistTotalMoney(memtId, totalMoney);
    }
}
