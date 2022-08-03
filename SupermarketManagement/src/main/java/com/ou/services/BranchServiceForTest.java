package com.ou.services;

import com.ou.pojos.Branch;
import com.ou.repositories.BranchRepositoryForTest;

import java.sql.SQLException;

public class BranchServiceForTest {

    private final static BranchRepositoryForTest BRANCH_REPOSITORY_FOR_TEST;

    static {
        BRANCH_REPOSITORY_FOR_TEST = new BranchRepositoryForTest();
    }

    // Lấy thông tin chi nhánh dựa vào id
    public Branch getBranchById(int braId) throws SQLException{
        if(braId <1 )
            return null;
        return BRANCH_REPOSITORY_FOR_TEST.getBranchById(braId);
    }
    // Lấy thông tin chi nhánh dựa vào tên chi nhánh
    public Branch getBranchByBraName(String braName) throws SQLException {
        return BRANCH_REPOSITORY_FOR_TEST.getBranchByBraName(braName.trim());
    }

}
