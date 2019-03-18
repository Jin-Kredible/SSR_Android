package com.shin.ssr.vo;

public class ProductVO {

    int age;
    int gender;
    int time;
    String trend_nm;
    String item_name;
    String item_price;
    String item_weight;
    String item_img_path;


    public int getAge() {
        return age;
    }
    public void setAge(int age) {
        this.age = age;
    }
    public int getGender() {
        return gender;
    }
    public void setGender(int gender) {
        this.gender = gender;
    }
    public int getTime() {
        return time;
    }
    public void setTime(int time) {
        this.time = time;
    }
    public String getTrend_nm() {
        return trend_nm;
    }
    public void setTrend_nm(String trend_nm) {
        this.trend_nm = trend_nm;
    }
    public String getItem_name() {
        return item_name;
    }
    public void setItem_name(String item_name) {
        this.item_name = item_name;
    }
    public String getItem_price() {
        return item_price;
    }
    public void setItem_price(String item_price) {
        this.item_price = item_price;
    }
    public String getItem_weight() {
        return item_weight;
    }
    public void setItem_weight(String item_weight) {
        this.item_weight = item_weight;
    }
    public String getItem_img_path() {
        return item_img_path;
    }
    public void setItem_img_path(String item_img_path) {
        this.item_img_path = item_img_path;
    }
    public ProductVO(int age, int gender, int time, String trend_nm, String item_name, String item_price,
                     String item_weight, String item_img_path) {
        super();
        this.age = age;
        this.gender = gender;
        this.time = time;
        this.trend_nm = trend_nm;
        this.item_name = item_name;
        this.item_price = item_price;
        this.item_weight = item_weight;
        this.item_img_path = item_img_path;
    }

    public ProductVO(String item_name, String item_price, String item_weight, String item_img_path) {
        this.item_name = item_name;
        this.item_price = item_price;
        this.item_weight = item_weight;
        this.item_img_path = item_img_path;
    }

    public ProductVO() {
        super();
    }
    @Override
    public String toString() {
        return "ProductVO [age=" + age + ", gender=" + gender + ", time=" + time + ", trend_nm=" + trend_nm
                + ", item_name=" + item_name + ", item_price=" + item_price + ", item_weight=" + item_weight
                + ", item_img_path=" + item_img_path + "]";
    }

}
