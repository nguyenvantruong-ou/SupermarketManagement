package com.ou.services;

import com.ou.pojos.Bill;
import com.ou.repositories.PaymentRepositoryForTest;

import java.sql.SQLException;

public class PaymentServiceForTest {

    private final static PaymentRepositoryForTest PAYMENT_REPOSITORY_FOR_TEST;
    static {
        PAYMENT_REPOSITORY_FOR_TEST = new PaymentRepositoryForTest();
    }
    // Lấy thông tin hóa đơn dựa vào id
    public Bill getBillByCreatedDate(String billCreatedDate) throws SQLException {
        return PAYMENT_REPOSITORY_FOR_TEST.getBillByCreatedDate(billCreatedDate);
    }
}
