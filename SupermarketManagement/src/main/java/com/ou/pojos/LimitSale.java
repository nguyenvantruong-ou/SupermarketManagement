package com.ou.pojos;

import java.util.Date;
import java.util.List;

public class LimitSale extends Sale{
    private Date lsalFromDate;
    private Date lsalToDate;
    private Sale sale;
    private List<ProductLimitSale> bills;
    private int amountProduct;

    public Date getLsalFromDate() {
        return lsalFromDate;
    }

    public void setLsalFromDate(Date lsalFromDate) {
        this.lsalFromDate = lsalFromDate;
    }

    public Date getLsalToDate() {
        return lsalToDate;
    }

    public void setLsalToDate(Date lsalToDate) {
        this.lsalToDate = lsalToDate;
    }

    public Sale getSale() {
        return sale;
    }

    public void setSale(Sale sale) {
        this.sale = sale;
    }

    public List<ProductLimitSale> getProLimitSales() {
        return bills;
    }

    public void setProductLimitSales(List<ProductLimitSale> bills) {
        this.bills = bills;
    }

    @Override
    public String toString() {
        return this.sale.toString();
    }

    public int getAmountProduct() {
        return amountProduct;
    }

    public void setAmountProduct(int amountProduct) {
        this.amountProduct = amountProduct;
    }
}
