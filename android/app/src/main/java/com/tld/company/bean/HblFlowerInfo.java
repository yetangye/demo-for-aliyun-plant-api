package com.tld.company.bean;

import java.util.Arrays;

public class HblFlowerInfo {
    private String Name;
    private String LatinName;
    private String AliasName;
    private String Family;
    private String Genus;
    private double Score;
    private String ImageUrl;
    private String[] AliasList;

    private String htmlDescV;

    public String getLatinName() {
        return LatinName;
    }

    public void setLatinName(String latinName) {
        LatinName = latinName;
    }

    public String getAliasName() {
        return AliasName;
    }

    public void setAliasName(String aliasName) {
        AliasName = aliasName;
    }

    public String getFamily() {
        return Family;
    }

    public void setFamily(String family) {
        Family = family;
    }

    public String getGenus() {
        return Genus;
    }

    public void setGenus(String genus) {
        Genus = genus;
    }

    public double getScore() {
        return Score;
    }

    public void setScore(double score) {
        Score = score;
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        ImageUrl = imageUrl;
    }

    public String[] getAliasList() {
        return AliasList;
    }

    public void setAliasList(String[] aliasList) {
        AliasList = aliasList;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getHtmlDescV() {
        return htmlDescV;
    }

    public void setHtmlDescV(String htmlDescV) {
        this.htmlDescV = htmlDescV;
    }

    @Override
    public String toString() {
        return "HblFlowerInfo{" +
                "Name='" + Name + '\'' +
                ", LatinName='" + LatinName + '\'' +
                ", AliasName='" + AliasName + '\'' +
                ", Family='" + Family + '\'' +
                ", Genus='" + Genus + '\'' +
                ", Score=" + Score +
                ", ImageUrl='" + ImageUrl + '\'' +
                ", AliasList=" + Arrays.toString(AliasList) +
                '}';
    }
}
