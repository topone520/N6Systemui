package com.adayo.systemui.bean;

public class ScenariomodeBean {
    int image;
    String scenar;
    String name;
    @Override
    public String toString() {
        return "ScenariomodeBean{" +
                "image=" + image +
                ", scenar='" + scenar + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

    public ScenariomodeBean() {

    }

    public ScenariomodeBean(int image, String scenar, String name) {
        this.image = image;
        this.scenar = scenar;
        this.name = name;
    }

    public int getImage() {
        return image;
    }

    public String getScenar() {
        return scenar;
    }

    public void setScenar(String scenar) {
        this.scenar = scenar;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int setImage(int image) {
        this.image = image;
        return 0;
    }

}
