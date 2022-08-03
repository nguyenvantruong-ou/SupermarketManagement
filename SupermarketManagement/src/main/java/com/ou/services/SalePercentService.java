package com.ou.services;

import com.ou.pojos.SalePercent;
import com.ou.repositories.SalePercentRepository;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;


public class SalePercentService {
    private final static SalePercentRepository SALE_PERCENT_REPOSITORY;

    static {
        SALE_PERCENT_REPOSITORY = new SalePercentRepository();
    }

    // Lấy danh sách các mã giảm giá
    public List<SalePercent> getSalePercents(int kw) throws SQLException {
        return SALE_PERCENT_REPOSITORY.getSalePercents(kw);
    }

    // Lấy số lượng mã giảm giá
    public int getSalePercentAmount() throws SQLException {
        return SALE_PERCENT_REPOSITORY.getSalePercentAmount();
    }

    // Lấy thông tin giảm giá dựa vào id
    public SalePercent getSalePercentById(Integer sperId) throws SQLException {
        if (sperId ==null)
            return null;
        return SALE_PERCENT_REPOSITORY.getSalePercentById(sperId);
    }

    // Lấy thông tin giảm giá dựa vào % giảm giá
    public SalePercent getSalePercentByPercent(Integer sperPercent) throws SQLException {
        if (sperPercent == null || sperPercent < 0 || sperPercent >= 100)
            return null;
        return SALE_PERCENT_REPOSITORY.getSalePercentByPercent(sperPercent);
    }

    // Thêm mã giảm giá
    public boolean addSalePercent(SalePercent salePercent) throws SQLException {
        if (salePercent == null || salePercent.getSperPercent() == null || salePercent.getSperPercent() < 0 || salePercent.getSperPercent() > 100)
            return false;
        if (SALE_PERCENT_REPOSITORY.isExistSalePercent(salePercent)) {
            SalePercent salePercentAdd = SALE_PERCENT_REPOSITORY.getSalePercents(
                    salePercent.getSperPercent()).get(0);
            if (!salePercentAdd.getSperIsActive())
                return SALE_PERCENT_REPOSITORY.addSalePercent(salePercentAdd);
            return false;
        }
        return SALE_PERCENT_REPOSITORY.addSalePercent(salePercent);
    }

    // Chỉnh sửa thông tin mã giảm giá
    public boolean updateSalePercent(SalePercent salePercent) throws SQLException {
        if (salePercent == null || salePercent.getSperPercent() == null ||
                salePercent.getSperPercent() < 0 || salePercent.getSperPercent() >= 100)
            return false;
        if (!SALE_PERCENT_REPOSITORY.isExistSalePercent(salePercent.getSperId()))
            return false;
        if (SALE_PERCENT_REPOSITORY.isExistSalePercent(salePercent)) {
            SalePercent existSper = SALE_PERCENT_REPOSITORY.getSalePercentByPercent(salePercent.getSperPercent());
            if (!Objects.equals(existSper.getSperId(), salePercent.getSperId()))
                return false;
        }
        return SALE_PERCENT_REPOSITORY.updateSalePercent(salePercent);
    }

    // Xóa mã giảm giá
    public boolean deleteSalePercent(SalePercent salePercent) throws SQLException {
        if (salePercent == null || salePercent.getSperId() == null||
                salePercent.getSperPercent() < 0 || salePercent.getSperPercent() >= 100)
            return false;
        if (!SALE_PERCENT_REPOSITORY.isExistSalePercent(salePercent.getSperId()))
            return false;
        return SALE_PERCENT_REPOSITORY.deleteSalePercent(salePercent);
    }

    // Lấy thông tin các mã giảm giá còn hoạt động
    public List<SalePercent> getAllActiveSalePercent() throws SQLException {
        return SALE_PERCENT_REPOSITORY.getAllActiveSalePercent();
    }

    // kiểm tra username có đang hoạt động hay không
    public boolean isActive(Integer sperId) throws SQLException {
        return SALE_PERCENT_REPOSITORY.isActive(sperId);
    }
}
