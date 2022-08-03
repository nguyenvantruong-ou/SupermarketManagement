package com.ou.services;

import com.ou.pojos.Staff;
import com.ou.repositories.StaffRepository;
import com.ou.utils.MD5Utils;

import java.sql.SQLException;
import java.util.List;

public class StaffService {
    private final static StaffRepository STAFF_REPOSITORY;
    static {
        STAFF_REPOSITORY  = new StaffRepository();
    }
    //Lấy thông tin của người
    public Staff getStaffById(Integer staId) throws SQLException {
        if (staId==null)
            return null;
        return STAFF_REPOSITORY.getStaffById(staId);
    }

    // Lấy danh sách staff theo tên
    public List<Staff> getListStaff(String kw) throws SQLException {
        return STAFF_REPOSITORY.getListStaff(kw);
    }

    // Lấy danh sách các tên nhánh
    public List<String> getListBranch() throws SQLException {
        return STAFF_REPOSITORY.getListBranch();
    }

    // Kiểm tra username có tồn tịa hay chưa
    public boolean isUsername(String username) throws SQLException {
        if(username.isEmpty())
            return false;
        return STAFF_REPOSITORY.isUsername(username);
    }

    // kiểm tra username có đang hoạt động hay không
    public boolean isActive(String username) throws SQLException {
        return STAFF_REPOSITORY.isActive(username);
    }

    // active = true
    public boolean setActive(String username) throws SQLException {
        return STAFF_REPOSITORY.setActive(username);
    }

    // thêm staff
    public boolean addStaff(Staff staff) throws SQLException {
        try {
            staff.setStaPassword(MD5Utils.getMd5(staff.getStaPassword()));
        } catch(NullPointerException e ){
            return false;
        }
        return STAFF_REPOSITORY.addStaff(staff);
    }

    // kiểm tra số CMND có trùng không
    public  boolean isExistCardId(String card_id) throws SQLException {
        if (card_id != "")
            return STAFF_REPOSITORY.isExistCardId(card_id);
        return false;
    }

    // lấy staff theo id
    public Staff getStaffDataById(Integer id) {
        if (id != 0) {
            try {
                return STAFF_REPOSITORY.getStaffDataById(id);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    // cập nhật staff
    public boolean updateStaff(Staff staff) {
        try {
            return STAFF_REPOSITORY.updateStaff(staff);
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // đặt lại mật khẩu là tên tài khoản
    public boolean resetPassword(String username) {
        if (!username.isEmpty()) {
            String password = MD5Utils.getMd5(username);
            try {
                return STAFF_REPOSITORY.resetPassword(username, password);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    // xóa staff
    public boolean deleteSatff(Integer id) {
        if (id != 0) {
            try {
                return STAFF_REPOSITORY.deleteStaff(id);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
    // Lấy thông tin nhân viên dựa vào username
    public Staff getStaffByUsername(String username) throws SQLException {
        if (username==null || username.isEmpty() ||
                !STAFF_REPOSITORY.isUsername(username.trim()) )
            return null;
        return STAFF_REPOSITORY.getStaffByUsername(username.trim());
    }
}
