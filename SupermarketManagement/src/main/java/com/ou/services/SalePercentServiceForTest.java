package com.ou.services;

import com.ou.pojos.SalePercent;
import com.ou.repositories.SalePercentRepositoryForTest;

import java.sql.SQLException;

public class SalePercentServiceForTest {
    private final static SalePercentRepositoryForTest SALE_REPOSITORY_FOR_TEST;

    static {
        SALE_REPOSITORY_FOR_TEST = new SalePercentRepositoryForTest();
    }

    // Lấy thông tin mã giảm giá dựa vào id
    public SalePercent getSalePercentById(int saleSperId) throws SQLException {
        if(saleSperId < 1 )
            return null;
        return SALE_REPOSITORY_FOR_TEST.getSalePercentById(saleSperId);
    }

    // Lấy thông tin mã giảm giá dựa vào % giảm giá
    public SalePercent getSalePercentByPercent(int saleSperPercent) throws SQLException {
        if(saleSperPercent < 0 || saleSperPercent > 100)
            return null;
        return SALE_REPOSITORY_FOR_TEST.getSalePercentByPercent(saleSperPercent);
    }
}
