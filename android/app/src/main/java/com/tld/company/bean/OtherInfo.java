package com.tld.company.bean;

public class OtherInfo {
    private String xgsc;  //相关诗词
    private String jzgy;  //价值功用
    private String hyyy;  //花语寓意
    private String fbdq;  //分布地区
    private String mcll;  //名称来历
    private String yhjs;  //养护技术
    private String bxtz;  //表型特征
    private String hksj;  //花开时节

    public String getXgsc() {
        return xgsc;
    }

    public void setXgsc(String xgsc) {
        this.xgsc = xgsc;
    }

    public String getJzgy() {
        return jzgy;
    }

    public void setJzgy(String jzgy) {
        this.jzgy = jzgy;
    }

    public String getHyyy() {
        return hyyy;
    }

    public void setHyyy(String hyyy) {
        this.hyyy = hyyy;
    }

    public String getFbdq() {
        return fbdq;
    }

    public void setFbdq(String fbdq) {
        this.fbdq = fbdq;
    }

    public String getMcll() {
        return mcll;
    }

    public void setMcll(String mcll) {
        this.mcll = mcll;
    }

    public String getYhjs() {
        return yhjs;
    }

    public void setYhjs(String yhjs) {
        this.yhjs = yhjs;
    }

    public String getBxtz() {
        return bxtz;
    }

    public void setBxtz(String bxtz) {
        this.bxtz = bxtz;
    }

    public String getHksj() {
        return hksj;
    }

    public void setHksj(String hksj) {
        this.hksj = hksj;
    }

    @Override
    public String toString() {
        return "相关诗词：" + xgsc +
                "\n价值功用：" + jzgy +
                "\n花语寓意：" + hyyy +
                "\n分布地区：" + fbdq +
                "\n名称来历：" + mcll +
                "\n养护技术：" + yhjs +
                "\n表型特征：" + bxtz +
                "\n花开时节：" + hksj;

    }
}


