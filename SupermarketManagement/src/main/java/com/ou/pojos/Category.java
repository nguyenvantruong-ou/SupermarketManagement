package com.ou.pojos;

import java.util.List;

public class Category {
    private Integer catId;
    private String catName;
    private Boolean catIsActive  = Boolean.TRUE;
    private List<Product> products;
    private Integer productAmount;
    public Integer getCatId() {
        return catId;
    }

    public void setCatId(Integer catId) {
        this.catId = catId;
    }

    public String getCatName() {
        return catName;
    }

    public void setCatName(String catName) {
        this.catName = catName;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public Boolean getCatIsActive() {
        return catIsActive;
    }

    public void setCatIsActive(Boolean catIsActive) {
        this.catIsActive = catIsActive;
    }

    public String toString() {
        return this.catName;
    }

    public Integer getProductAmount() {
        return productAmount;
    }

    public void setProductAmount(Integer productAmount) {
        this.productAmount = productAmount;
    }
}
