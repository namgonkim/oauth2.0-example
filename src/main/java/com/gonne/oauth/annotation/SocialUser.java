package com.gonne.oauth.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * Created by zxcv9455@naver.com on 2021-02-09
 * Blog : http://velog.io/@namgonkim
 * Github : http://github.com/namgonkim
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface SocialUser {
}
