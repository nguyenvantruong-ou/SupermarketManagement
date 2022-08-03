package com.ou.pojos;

import java.math.BigDecimal;
import java.util.List;

public class Member extends Person{
    private BigDecimal memTotalPurchase;
    private MemberType memberType;
    private List<Bill> bills;

    public BigDecimal getMemTotalPurchase() {
        return memTotalPurchase;
    }

    public void setMemTotalPurchase(BigDecimal memTotalPurchase) {
        this.memTotalPurchase = memTotalPurchase;
    }

    public MemberType getMemberType() {
        return memberType;
    }

    public void setMemberType(MemberType memberType) {
        this.memberType = memberType;
    }

    public List<Bill> getBills() {
        return bills;
    }

    public void setBills(List<Bill> bills) {
        this.bills = bills;
    }
}
