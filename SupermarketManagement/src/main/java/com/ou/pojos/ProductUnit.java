package com.ou.pojos;

import java.math.BigDecimal;

public class ProductUnit {
    private Integer pruId;
    private BigDecimal proPrice;
    private Product product;
    private Unit unit;
    private Boolean pruIsActive = true;

    public BigDecimal getProPrice() {
        return proPrice;
    }

    public void setProPrice(BigDecimal proPrice) {
        this.proPrice = proPrice;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    public Integer getPruId() {
        return pruId;
    }

    public void setPruId(Integer pruId) {
        this.pruId = pruId;
    }

    public Boolean getPruIsActive() {
        return pruIsActive;
    }

    public void setPruIsActive(Boolean pruIsActive) {
        this.pruIsActive = pruIsActive;
    }
}
