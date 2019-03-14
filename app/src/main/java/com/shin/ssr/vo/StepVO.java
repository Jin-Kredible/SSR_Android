package com.shin.ssr.vo;

public class StepVO {


    int user_id, gender, age, wk_am, wk_sa;
    String name, wk_dt;



    public int getUser_id() {
        return user_id;
    }
    public void setUser_id(int user_id) {
        this.user_id = user_id;
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
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public int getWk_am() {
        return wk_am;
    }
    public void setWk_am(int wk_am) {
        this.wk_am = wk_am;
    }
    public int getWk_sa() {
        return wk_sa;
    }
    public void setWk_sa(int wk_sa) {
        this.wk_sa = wk_sa;
    }
    public String getWk_dt() {
        return wk_dt;
    }
    public void setWk_dt(String wk_dt) {
        this.wk_dt = wk_dt;
    }


    public StepVO(int user_id, int gender, int age, int wk_am, int wk_sa, String name, String wk_dt) {
        super();
        this.user_id = user_id;
        this.gender = gender;
        this.age = age;
        this.wk_am = wk_am;
        this.wk_sa = wk_sa;
        this.name = name;
        this.wk_dt = wk_dt;
    }

    public StepVO(int user_id,  int wk_am,  String wk_dt) {
        super();
        this.user_id = user_id;
        this.wk_am = wk_am;
        this.wk_dt = wk_dt;
    }
    public StepVO() {
        super();
    }
    @Override
    public String toString() {
        return "StepVO [user_id=" + user_id + ", gender=" + gender + ", age=" + age + ", wk_am=" + wk_am + ", wk_sa="
                + wk_sa + ", name=" + name + ", wk_dt=" + wk_dt + "]";
    }





}

