package com.caitu99.gateway.oauth;

/**
 * Created by ws on 2015/10/29.
 */
public enum LoginType {
    MOBILE("0"),QQ("1"),WEBO("2"),WEICHAT("3"),ACCOUNT("4"),WEICHAT_IDEN("5");

    private String value;
    LoginType(String value)
    {
        this.value = value;
    }
    public String getValue(){return value;}
}
