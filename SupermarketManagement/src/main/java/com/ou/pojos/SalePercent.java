package com.ou.pojos;

import java.util.List;

public class SalePercent {
    private Integer sperId;
    private Integer sperPercent;
    private Boolean sperIsActive = Boolean.TRUE;
    private List<Sale> sales;

    public Integer getSperId() {
        return sperId;
    }

    public void setSperId(Integer sperId) {
        this.sperId = sperId;
    }

    public Integer getSperPercent() {
        return sperPercent;
    }

    public void setSperPercent(Integer sperPercent) {
        this.sperPercent = sperPercent;
    }

    public List<Sale> getSales() {
        return sales;
    }

    public void setSales(List<Sale> sales) {
        this.sales = sales;
    }

    public Boolean getSperIsActive() {
        return sperIsActive;
    }

    public void setSperIsActive(Boolean sperIsActive) {
        this.sperIsActive = sperIsActive;
    }

    @Override
    public String toString() {
        return sperPercent.toString() + "%";
    }
}
