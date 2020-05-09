package com.example.login;

public class Friend {
    private String aName;
    private String aSpeak;
    //private int aIcon;

    public Friend() {
    }

    public Friend(String aName, String aSpeak) {
        this.aName = aName;
        this.aSpeak = aSpeak;
        //this.aIcon = aIcon;
    }

    public String getaName() {
        return aName;
    }

    public String getaSpeak() {
        return aSpeak;
    }

//    public int getaIcon() {
//        return aIcon;
//    }

    public void setaName(String aName) {
        this.aName = aName;
    }

    public void setaSpeak(String aSpeak) {
        this.aSpeak = aSpeak;
    }

//    public void setaIcon(int aIcon) {
//        this.aIcon = aIcon;
//    }
}
