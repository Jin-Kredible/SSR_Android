package com.shin.ssr.vo;

public class ProductVO {

    private String productName;
    private int productPrice;
    private int productWeight;

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(int productPrice) {
        this.productPrice = productPrice;
    }

    public int getProductWeight() {
        return productWeight;
    }

    public void setProductWeight(int productWeight) {
        this.productWeight = productWeight;
    }

    @Override
    public String toString() {
        return "ProductVO{" +
                "productName=" + productName +
                ", productPrice=" + productPrice +
                ", productWeight=" + productWeight +
                '}';
    }

    public ProductVO(String productName, int productPrice, int productWeight) {
        this.productName = productName;
        this.productPrice = productPrice;
        this.productWeight = productWeight;
    }

    public ProductVO() {
    }
}
