package com.ou.services;

import com.ou.pojos.Sale;
import com.ou.repositories.SaleRepository;

import java.sql.SQLException;
import java.util.List;

public class SaleService {
    private final static SaleRepository SALE_REPOSITORY;
    static {
        SALE_REPOSITORY = new SaleRepository();
    }

    // Lấy thông tin Sale dựa vào id
    public Sale getSaleById(Integer saleId ) throws SQLException {
        if(saleId == null)
            return null;
        return SALE_REPOSITORY.getSaleById(saleId);
    }

    public List<Sale> getSales(String kw) throws SQLException {
        return SALE_REPOSITORY.getSales(kw);
    }

    public int getSaleAmount() throws SQLException {
        return SALE_REPOSITORY.getSaleAmount();
    }

    public List<String> getSalePercentId() throws SQLException {
        return SALE_REPOSITORY.getSalePercentId();
    }

    public boolean addSale(Sale sale) throws SQLException {
        if (sale == null || sale.getSalePercent().getSperId() == null)
            return false;
        return SALE_REPOSITORY.addSale(sale);
    }

    public boolean updateSale(Sale sale) throws SQLException {
        if (sale == null || sale.getSalePercent().getSperId() == null)
            return false;
        return SALE_REPOSITORY.updateSale(sale);
    }

    public boolean deleteSale(Sale sale) throws SQLException {
        if (sale == null || sale.getSaleId() == null || !sale.getSaleIsActive())
            return false;
        return SALE_REPOSITORY.deleteSale(sale);
    }

    public float getSalePercentBySperId(int sperId){
        if(sperId < 0)
            return 0;
        return SALE_REPOSITORY.getSalePercentBySperId(sperId);
    }
}
