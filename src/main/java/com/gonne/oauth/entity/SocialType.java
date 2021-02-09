package com.gonne.oauth.entity;

import java.util.Locale;
/**
 * Created by zxcv9455@naver.com on 2021-02-09
 * Blog : http://velog.io/@namgonkim
 * Github : http://github.com/namgonkim
 */
public enum SocialType {

    GOOGLE("google");

    private final String ROLE_PREFIX = "ROLE_";
    private String name;

    SocialType(String name) {
        this.name = name;
    }

    public String getRoleType() {
        return ROLE_PREFIX + name.toUpperCase();
    }

    public String getValue() {
        return name;
    }

    public boolean isEquals(String authority) {
        return this.getRoleType().equals(authority);
    }
}
