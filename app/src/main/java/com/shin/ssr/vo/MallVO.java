package com.shin.ssr.vo;

public class MallVO {

    private String mMallNmae;

    public MallVO(String mMallNmae, double mlatitude, double longitude) {
        this.mMallNmae = mMallNmae;
        this.mlatitude = mlatitude;
        this.longitude = longitude;
    }

    private double mlatitude;
    private double longitude;

    public void setMallVO(String name, double lati, double longi){
        mMallNmae = name;
        mlatitude = lati;
        longitude = longi;
    }

    public String getmMallNmae() {
        return mMallNmae;
    }

    public void setmMallNmae(String mMallNmae) {
        this.mMallNmae = mMallNmae;
    }

    public double getMlatitude() {
        return mlatitude;
    }

    public void setMlatitude(double mlatitude) {
        this.mlatitude = mlatitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
