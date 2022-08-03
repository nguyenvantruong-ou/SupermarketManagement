package com.ou.services;

import com.ou.pojos.LimitSale;
import com.ou.repositories.LimitSaleRepository;

import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

public class LimitSaleService {
    private final static LimitSaleRepository LIMIT_SALE_REPOSITORY;
    static {
        LIMIT_SALE_REPOSITORY = new LimitSaleRepository();
    }

    // Lấy thông tin limit sale dựa vào id
    public LimitSale getLimitSaleById(Integer lsalId) throws SQLException {
        if (lsalId ==null)
            return null;
        return LIMIT_SALE_REPOSITORY.getLimitSaleById(lsalId);
    }
    // Lấy danh sách các loại giảm giá có thời hạn
    public List<LimitSale> getLimitSales() throws SQLException{
        return LIMIT_SALE_REPOSITORY.getLimitSales();
    }

    // Lấy danh sách các loại giảm giá có thời hạn theo from_date và to_date
    public List<LimitSale> getLimitSales(Date searchDate) throws SQLException {
        return LIMIT_SALE_REPOSITORY.getLimitSales(searchDate);
    }

    // lấy tổng sô mã giảm giá có thời hạn còn hoạt động
    public int getTotalAmountLimitSale() throws SQLException{
        return LIMIT_SALE_REPOSITORY.getTotalAmountLimitSale();
    }

    // lấy danh sách product id
    public List<String> getListProductId() throws SQLException {
        return LIMIT_SALE_REPOSITORY.getListProductId();
    }

    // lấy danh sách sale id
    public List<String> getListSaleId() throws SQLException {
        return LIMIT_SALE_REPOSITORY.getListSaleId();
    }

    // lây danh sách mã sản phẩm theo mã giảm giá
    public List<String> getIdProductByLsalId(int lsalId) throws SQLException {
        return LIMIT_SALE_REPOSITORY.getProductIdByLsalId(lsalId);
    }

    // lấy danh sách id sản phẩm không thuộc limit sale
    public List<String> getProductIdNotInLimitSaleByLsalId(int lsalId) throws SQLException {
        return LIMIT_SALE_REPOSITORY.getProductIdNotInLimitSaleByLsalId(lsalId);
    }

    // Lấy thông tin limit sale dựa vào id
    public LimitSale getLimitSaleByLsalId(int lsalId) throws SQLException {
        return LIMIT_SALE_REPOSITORY.getLimitSaleByLsalId(lsalId);
    }

    // kiểm tra xem lsal_id có tồn tại không
    public boolean isExitsLimitSale(int lsalId) throws SQLException {
        return LIMIT_SALE_REPOSITORY.isExitsLimitSale(lsalId);
    }

    // thêm một limit sale
    public boolean addLimitSale(LimitSale limitSale, int proId) throws SQLException {
        if(proId == 0 && !limitSale.getSaleIsActive())
            return LIMIT_SALE_REPOSITORY.addLimitSale(limitSale, proId);
        if(limitSale == null || proId == 0)
            return false;
        return LIMIT_SALE_REPOSITORY.addLimitSale(limitSale, proId);
    }

    // cập nhật thông tin limit sale
    public boolean updateLimitSale(LimitSale limitSale) throws SQLException {
        if(limitSale == null || limitSale.getSaleId() == null)
            return false;
        return LIMIT_SALE_REPOSITORY.updateLimitSale(limitSale);
    }

    // xóa thông tin limit sale
    public boolean deleteLimitSale(LimitSale limitSale, int proId) throws SQLException {
        if(limitSale == null || limitSale.getSaleId() == null)
            return false;
        return LIMIT_SALE_REPOSITORY.deleteLimitSale(limitSale, proId);
    }
}
