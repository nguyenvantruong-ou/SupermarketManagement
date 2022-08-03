package com.ou.pojos;

import java.util.List;

public class Product {
    private Integer proId;
    private String proName;
    private Boolean proIsActive = Boolean.TRUE;
    private List<ProductBranch> productBranches;
    private List<ProductUnit> productUnits;
    private List<ProductLimitSale> productLimitSales;
    private Category category;
    private Manufacturer manufacturer;

    public Integer getProId() {
        return proId;
    }

    public void setProId(Integer proId) {
        this.proId = proId;
    }

    public String getProName() {
        return proName;
    }

    public void setProName(String proName) {
        this.proName = proName;
    }

    public List<ProductBranch> getProductBranches() {
        return productBranches;
    }

    public void setProductBranches(List<ProductBranch> productBranches) {
        this.productBranches = productBranches;
    }

    public List<ProductUnit> getProductUnits() {
        return productUnits;
    }

    public void setProductUnits(List<ProductUnit> productUnits) {
        this.productUnits = productUnits;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Manufacturer getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(Manufacturer manufacturer) {
        this.manufacturer = manufacturer;
    }

    public Boolean getProIsActive() {
        return proIsActive;
    }

    public void setProIsActive(Boolean proIsActive) {
        this.proIsActive = proIsActive;
    }

    public List<ProductLimitSale> getProductLimitSales() {
        return productLimitSales;
    }

    public void setProductLimitSales(List<ProductLimitSale> productLimitSales) {
        this.productLimitSales = productLimitSales;
    }
}
