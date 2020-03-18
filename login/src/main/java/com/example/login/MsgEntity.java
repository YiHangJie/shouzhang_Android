package com.example.login;

public class MsgEntity {
    //分别代表发送和接受消息的类型
    public static final int SEND_MSG=1;
    public static final int RCV_MSG=2;

    public static final int IS_READ=1;
    public static final int UN_READ=0;

    //消息内容
    private String content;
    //消息类型
    private int type;
    //已读未读
    private int isread;

    public MsgEntity(int type, String content) {
        this.type = type;
        this.content = content;
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

    public void setIsRead(int isread)
    {
        this.isread = isread;
    }

}
