package com.example.login;

public class MsgEntity {
    //分别代表发送和接受消息的类型
    public static final int SEND_MSG=1;
    public static final int RCV_MSG=2;

    public static final int IS_READ=1;
    public static final int UN_READ=0;

    public static final int IS_Img=1;
    public static final int NOT_Img=0;

    //消息内容
    private String content;
    //消息类型
    private int type;
    //已读未读
    private int isread;
    //是否是照片
    private int isImg;
    private String time;
    private String Url;

    public MsgEntity(int type, String content) {
        this.type = type;
        this.content = content;
    }

    public MsgEntity(int type, String content,int isread) {
        this.type = type;
        this.content = content;
        this.isread = isread;
        this.isImg = NOT_Img;
        this.time = "";
        this.Url = "";
    }

    public MsgEntity(int type, String content,int isread,String Url,int isImg,String time) {
        this.type = type;
        this.content = content;
        this.isread = isread;
        this.isImg = isImg;
        this.time = time;
        this.Url = Url;
    }

    public String getContent() {
        return content;
    }

    public int getType() {
        return type;
    }

    public int getIsread()
    {
        return isread;
    }

    public int getIS_Img()
    {
        return isImg;
    }

    public void setIsRead(int isread)
    {
        this.isread = isread;
    }

    public String getUrl()
    {
        return Url;
    }

    public String getTime() {
        return time;
    }
}
