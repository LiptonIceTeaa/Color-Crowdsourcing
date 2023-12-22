package com.example.color_crowdsourcing;

public class ColorModel {
    private String name;
    private String hexValue;

    public ColorModel(String name, String hexValue) {
        this.name = name;
        this.hexValue = hexValue;
    }

    public String getName() {
        return name;
    }

    public String getHexValue() {
        return hexValue;
    }
}
