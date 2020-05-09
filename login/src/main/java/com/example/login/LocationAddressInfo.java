package com.example.login;

public class LocationAddressInfo {
    private String lon;
    private String lat;
    private String title;
    private String address;
    private String Typedes;
    private String Typecode;

    public LocationAddressInfo(String lon,String lat,String title,String text,String Typedes, String Typecode)
    {
        this.lon = lon;
        this.lat = lat;
        this.title = title;
        this.address = text;
        this.Typedes = Typedes;
        this.Typecode = Typecode;
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

    public String getAddress()
    {
        return address;
    }

    public String getTypedes()
    {
        return Typedes;
    }

    public String getTypecode()
    {
        return Typecode;
    }

}
