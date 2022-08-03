package com.ou.services;

import com.ou.pojos.MemberType;
import com.ou.repositories.MemberTypeRepositoryForTest;

import java.sql.SQLException;
import java.util.List;

public class MemberTypeServiceForTest {
    private final static MemberTypeRepositoryForTest MEMBER_TYPE_REPOSITORY_FOR_TEST;
    static {
        MEMBER_TYPE_REPOSITORY_FOR_TEST = new MemberTypeRepositoryForTest();
    }
    // Lấy thông tin  loại thành viên theo id
    public MemberType getMemberTypeById(int memtId) throws SQLException {
        if(memtId <1 )
            return null;
        return MEMBER_TYPE_REPOSITORY_FOR_TEST.getMemberTypeById(memtId);
    }
    // Lấy thông tin  loại thành viên theo têm
    public MemberType getMemberTypeByName(String name) throws SQLException {
        return MEMBER_TYPE_REPOSITORY_FOR_TEST.getMemberTypeByName(name);
    }

    // lấy danh sách loại thành viên còn hoạt động
    public List<MemberType> getMemberTypes() throws SQLException {
        return MEMBER_TYPE_REPOSITORY_FOR_TEST.getMemberTypes();
    }
}
