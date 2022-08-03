package com.ou.services;

import com.ou.pojos.Sale;
import com.ou.repositories.SaleRepositoryForTest;

import java.sql.SQLException;

public class SaleServiceForTest {
    private static final SaleRepositoryForTest SALE_REPOSITORY_FOR_TEST;
    static {
        SALE_REPOSITORY_FOR_TEST = new SaleRepositoryForTest();
    }
    //lấy mã giảm giá theo id
    public Sale getSaleById(int saleId) throws SQLException {
        if(saleId < 1)
            return null;
        return SALE_REPOSITORY_FOR_TEST.getSaleById(saleId);
    }
}
