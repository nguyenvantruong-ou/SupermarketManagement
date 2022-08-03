package com.ou.pojos;

import java.util.List;

public class Branch {
    private Integer braId;
    private String braName;
    private String braAddress;
    private Boolean braIsActive = Boolean.TRUE;
    private List<ProductBranch> productBranches;
    private List<Staff> staffs;

    private int productAmount;
    private  int staffAmount;

    public Integer getBraId() {
        return braId;
    }

    public void setBraId(Integer braId) {
        this.braId = braId;
    }

    public String getBraName() {
        return braName;
    }

    public void setBraName(String braName) {
        this.braName = braName;
    }

    public String getBraAddress() {
        return braAddress;
    }

    public void setBraAddress(String braAddress) {
        this.braAddress = braAddress;
    }

    public List<ProductBranch> getProductBranches() {
        return productBranches;
    }

    public void setProductBranches(List<ProductBranch> productBranches) {
        this.productBranches = productBranches;
    }

    public List<Staff> getStaffs() {
        return staffs;
    }

    public void setStaffs(List<Staff> staffs) {
        this.staffs = staffs;
    }

    public int getProductAmount() {
        return productAmount;
    }

    public void setProductAmount(int productAmount) {
        this.productAmount = productAmount;
    }

    public int getStaffAmount() {
        return staffAmount;
    }

    public void setStaffAmount(int staffAmount) {
        this.staffAmount = staffAmount;
    }

    public Boolean getBraIsActive() {
        return braIsActive;
    }

    public void setBraIsActive(Boolean braIsActive) {
        this.braIsActive = braIsActive;
    }

    public String toString(){
        return braName;
    }
}
