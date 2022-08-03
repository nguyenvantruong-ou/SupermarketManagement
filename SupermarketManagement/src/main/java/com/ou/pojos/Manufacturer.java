package com.ou.pojos;

import java.util.List;

public class Manufacturer {
    private Integer manId;
    private String manName;
    private Boolean manIsActive  = Boolean.TRUE;
    private Integer productAmount;
    private List<Product> products;

    public Integer getManId() {
        return manId;
    }

    public void setManId(Integer manId) {
        this.manId = manId;
    }

    public String getManName() {
        return manName;
    }

    public void setManName(String manName) {
        this.manName = manName;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public Boolean getManIsActive() {
        return manIsActive;
    }

    public void setManIsActive(Boolean manIsActive) {
        this.manIsActive = manIsActive;
    }

    public Integer getProductAmount() {
        return productAmount;
    }

    public void setProductAmount(Integer productAmount) {
        this.productAmount = productAmount;
    }
    public String toString(){
        return this.manName;
    }
}
