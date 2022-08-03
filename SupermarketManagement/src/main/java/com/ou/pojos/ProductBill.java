package com.ou.pojos;

public class ProductBill {
    private Integer proAmount;
    private ProductUnit productUnit;
    private Bill bill;

    public Integer getProAmount() {
        return proAmount;
    }

    public void setProAmount(Integer proAmount) {
        this.proAmount = proAmount;
    }

    public ProductUnit getProductUnit() {
        return productUnit;
    }

    public void setProductUnit(ProductUnit productUnit) {
        this.productUnit = productUnit;
    }

    public Bill getBill() {
        return bill;
    }

    public void setBill(Bill bill) {
        this.bill = bill;
    }
}
