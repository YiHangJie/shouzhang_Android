package com.example.login;

public class LocationAddressInfo {
    private String lon;
    private String lat;
    private String title;
    private String text;

    public LocationAddressInfo(String lon,String lat,String title,String text)
    {
        this.lon = lon;
        this.lat = lat;
        this.title = title;
        this.text = text;
    }

    public String getLon()
    {
        return lon;
    }

    public String getLat()
    {
        return lat;
    }

    public String getTitle()
    {
        return title;
    }

    public String getText()
    {
        return text;
    }

}
