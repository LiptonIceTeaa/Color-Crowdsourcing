package com.example.color_crowdsourcing;

import java.util.Locale;

public class user {

    private String name;
    private String country;
    private String points;

    public user(String name, String country, String points) {
        this.name = name;
        this.country = country;
        this.points = points;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPoints() {
        return points;
    }

    public void setPoints(String points) {
        this.points = points;
    }

    public String getCountryCode() {
        String countryCode = "";
        Locale[] locales = Locale.getAvailableLocales();

        for (Locale locale : locales) {
            if (this.country.equalsIgnoreCase(locale.getDisplayCountry())) {
                countryCode = locale.getCountry();
                break;
            }
        }

        return countryCode;
    }





}
