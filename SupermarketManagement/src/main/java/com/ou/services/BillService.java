package com.ou.services;

import com.ou.pojos.Bill;
import com.ou.pojos.ProductBill;
import com.ou.repositories.BillRepository;
import com.ou.utils.PersonType;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BillService {
    private final static BillRepository BILL_REPOSITORY;

    static {
        BILL_REPOSITORY = new BillRepository();
    }

    // Lấy danh sách hóa đơn do nhân viên tạo dựa vào tên nhân viên
    public List<Bill> getBills(String name, PersonType persType, List<String> dates) throws SQLException {
        return BILL_REPOSITORY.getBills(name, persType, dates == null ? new ArrayList<>() : dates);
    }

    // Lấy số lượng hóa đơn
    public int getBillAmount() throws SQLException {
        return BILL_REPOSITORY.getBillAmount();
    }

    // Lấy danh sách sản phẩm tương ứng của hóa đơn
    public List<ProductBill> getProductBillsByBillId(int billId) throws SQLException {
        return BILL_REPOSITORY.getProductBillsByBillId(billId);
    }

    // Lấy thông tin ngày tạo hóa đơn
    public Date getCreatedDateBill(int billId) throws SQLException {
        return BILL_REPOSITORY.getCreatedDateBill(billId);
    }
}