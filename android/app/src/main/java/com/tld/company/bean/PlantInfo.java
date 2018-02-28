package com.tld.company.bean;

import java.util.Arrays;

public class PlantInfo {
    private String nameStd;     //标准中文名
    private String nameLt;      //拉丁名
    private String familyCn;    //中文科
    private String genusCn;     //中文属
    private String alias;       //中文别名。如果有多个，用逗号分隔
    private String description; //简要介绍
    private OtherInfo info;     //其他信息
    private String[] images;    //代表图像

    public String getNameStd() {
        return nameStd;
    }

    public void setNameStd(String nameStd) {
        this.nameStd = nameStd;
    }

    public String getNameLt() {
        return nameLt;
    }

    public void setNameLt(String nameLt) {
        this.nameLt = nameLt;
    }

    public String getFamilyCn() {
        return familyCn;
    }

    public void setFamilyCn(String familyCn) {
        this.familyCn = familyCn;
    }

    public String getGenusCn() {
        return genusCn;
    }

    public void setGenusCn(String genusCn) {
        this.genusCn = genusCn;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public OtherInfo getInfo() {
        return info;
    }

    public void setInfo(OtherInfo info) {
        this.info = info;
    }

    public String[] getImages() {
        return images;
    }

    public void setImages(String[] images) {
        this.images = images;
    }

    @Override
    public String toString() {
        return "中文名：" + nameStd +
                "\n拉丁名：" + nameLt +
                "\n中文科：" + familyCn +
                "\n中文属：" + genusCn +
                "\n中文别名：" + alias +
                "\n简要介绍：" + description +
                "\n" + info.toString() +
                "\n代表图像：" + Arrays.toString(images);

    }
}
