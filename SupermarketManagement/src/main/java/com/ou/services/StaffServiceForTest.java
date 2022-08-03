package com.ou.services;

import com.ou.pojos.Staff;
import com.ou.repositories.StaffRepositoryForTest;

import java.sql.SQLException;
import java.util.List;

public class StaffServiceForTest {
    private final static StaffRepositoryForTest STAFF_REPOSITORY_FOR_TEST;
    static {
        STAFF_REPOSITORY_FOR_TEST  = new StaffRepositoryForTest();
    }

    // lấy lấy danh sách username
    public List<Staff> getListUsernameStaff() throws SQLException {
        return STAFF_REPOSITORY_FOR_TEST.getListUsernameStaff();
    }

    // lấy mật khẩu theo username
    public String getPasswordStaff(String username) throws SQLException {
        if (username != "")
            return STAFF_REPOSITORY_FOR_TEST.getPasswordStaff(username);
        return "";
    }

    // kiểm tra active theo sta_id
    public boolean isActiveById(Integer id) throws SQLException {
        if (id != 0) {
            return STAFF_REPOSITORY_FOR_TEST.isActiveById(id);
        }
        return false;
    }

    // kiểm tra có phải admin hay không
    public boolean isAdminByUsername (String username) {
        if (!username.isEmpty()) {
            try {
                return STAFF_REPOSITORY_FOR_TEST.isAdminByUsername(username);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
