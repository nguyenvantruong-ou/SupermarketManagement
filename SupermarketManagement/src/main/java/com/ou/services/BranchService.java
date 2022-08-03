package com.ou.services;

import com.ou.pojos.Branch;
import com.ou.repositories.BranchRepository;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

public class BranchService {
    private final static BranchRepository BRANCH_REPOSITORY;

    static {
        BRANCH_REPOSITORY = new BranchRepository();
    }

    // Lấy danh sách các chi nhánh
    public List<Branch> getBranches(String kw) throws SQLException {
        return BRANCH_REPOSITORY.getBranches(kw);
    }

    // Lấy số lượng chi nhánh
    public int getBranchAmount() throws SQLException {
        return BRANCH_REPOSITORY.getBranchAmount();
    }

    // Thêm chi nhánh
    public boolean addBranch(Branch branch) throws SQLException {
        if (branch == null ||
                branch.getBraName() == null || branch.getBraName().trim().isEmpty() ||
                branch.getBraName().trim().length() >= 100 ||
                branch.getBraAddress() == null || branch.getBraAddress().trim().isEmpty() ||
                branch.getBraAddress().trim().length() >= 150 ||
                BRANCH_REPOSITORY.isExistAddress(branch.getBraAddress().trim()))
            return false;
        if (BRANCH_REPOSITORY.isExistBranch(branch)) {
            Branch branchAdd = BRANCH_REPOSITORY.getBranches(branch.getBraName()
                    .trim()).get(0);
            if (!branchAdd.getBraIsActive())
                return BRANCH_REPOSITORY.addBranch(branchAdd);
            return false;
        }
        return BRANCH_REPOSITORY.addBranch(branch);
    }

    // Chỉnh sửa thông tin chi nhánh
    public boolean updateBranch(Branch branch) throws SQLException {
        if (branch == null ||
                branch.getBraId() == null ||
                branch.getBraName() == null || branch.getBraName().trim().isEmpty() ||
                branch.getBraName().trim().length() >= 100 ||
                branch.getBraAddress() == null || branch.getBraAddress().trim().isEmpty() ||
                branch.getBraAddress().trim().length() >= 150)
            return false;
        if (!BRANCH_REPOSITORY.isExistBranch(branch.getBraId()))
            return false;
        try {
           return BRANCH_REPOSITORY.updateBranch(branch);
        }catch (SQLException sqlException){
            return false;
        }
    }

    // Xóa chi nhánh
    public boolean deleteBranch(Branch branch) throws SQLException {
        if (branch == null || branch.getBraId() == null)
            return false;
        if (!BRANCH_REPOSITORY.isExistBranch(branch.getBraId()))
            return false;
        return BRANCH_REPOSITORY.deleteBranch(branch);
    }

    // Lấy thông tin các chi nhánh còn hoạt động
    public List<Branch> getAllActiveBranch() throws SQLException {
        return BRANCH_REPOSITORY.getAllActiveBranch();
    }

    // Lấy thông tin của chi nhánh dựa vào tên chi nhánh
    public Branch getBranchByName(String braName) throws SQLException {
        if (braName == null)
            return null;
        return BRANCH_REPOSITORY.getBranchByName(braName);
    }
}
