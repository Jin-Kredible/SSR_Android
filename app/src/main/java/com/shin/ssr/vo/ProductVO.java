package com.shin.ssr.vo;

public class ProductVO {

    private int gender;
    private int age;
    private int time;

    public ProductVO(int gender, int age, int time) {
        this.gender = gender;
        this.age = age;
        this.time = time;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }
}
