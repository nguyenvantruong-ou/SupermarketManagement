package com.ou.pojos;

import java.util.List;

public class Unit {
    private Integer uniId;
    private String uniName;
    private Boolean uniIsActive = Boolean.TRUE;
    private List<ProductUnit> productUnits;

    public Integer getUniId() {
        return uniId;
    }

    public void setUniId(Integer uniId) {
        this.uniId = uniId;
    }

    public String getUniName() {
        return uniName;
    }

    public void setUniName(String uniName) {
        this.uniName = uniName;
    }

    public List<ProductUnit> getProductUnits() {
        return productUnits;
    }

    public void setProductUnits(List<ProductUnit> productUnits) {
        this.productUnits = productUnits;
    }

    public Boolean getUniIsActive() {
        return uniIsActive;
    }

    public void setUniIsActive(Boolean uniIsActive) {
        this.uniIsActive = uniIsActive;
    }

    public String toString(){
        return uniName;
    }
}
