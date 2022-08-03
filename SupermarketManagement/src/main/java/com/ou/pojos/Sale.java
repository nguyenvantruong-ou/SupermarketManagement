package com.ou.pojos;

import java.util.List;

public class Sale {
    protected Integer saleId;
    protected SalePercent salePercent;
    protected Boolean saleIsActive = Boolean.TRUE;
    private List<MemberType> memberTypes;

    public Integer getSaleId() {
        return saleId;
    }

    public void setSaleId(Integer saleId) {
        this.saleId = saleId;
    }

    public SalePercent getSalePercent() {
        return salePercent;
    }

    public void setSalePercent(SalePercent salePercent) {
        this.salePercent = salePercent;
    }

    public List<MemberType> getMemberTypes() {
        return memberTypes;
    }

    public void setMemberTypes(List<MemberType> memberTypes) {
        this.memberTypes = memberTypes;
    }

    public Boolean getSaleIsActive() {
        return saleIsActive;
    }

    public void setSaleIsActive(Boolean saleIsActive) {
        this.saleIsActive = saleIsActive;
    }

    public String toString(){
        return salePercent.toString();
    }
}
