package com.ou.services;

import com.ou.pojos.Unit;
import com.ou.repositories.UnitRepositoryForTest;

import java.sql.SQLException;

public class UnitServiceForTest {
    private final static UnitRepositoryForTest UNIT_REPOSITORY_FOR_TEST;

    static {
        UNIT_REPOSITORY_FOR_TEST = new UnitRepositoryForTest();
    }

    // Lấy thông tin đơn vị dựa vào id
    public Unit getUnitById(int uniId) throws SQLException {
        return UNIT_REPOSITORY_FOR_TEST.getUnitById(uniId);
    }
    // Lấy thông tin đơn vị dựa vào tên
    public Unit getUnitByName (String uniName) throws SQLException{
        return UNIT_REPOSITORY_FOR_TEST.getUnitByName(uniName.trim());
    }
}
