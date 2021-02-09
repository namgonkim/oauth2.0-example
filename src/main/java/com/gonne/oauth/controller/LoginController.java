package com.gonne.oauth.controller;

import com.gonne.oauth.annotation.SocialUser;
import com.gonne.oauth.entity.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Created by zxcv9455@naver.com on 2021-02-09
 * Blog : http://velog.io/@namgonkim
 * Github : http://github.com/namgonkim
 */
@Controller
public class LoginController {
    @GetMapping({"","/"})
    public String getAuthorizationMessage() {
        return "home";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/board")
    public String board() {
        return "board";
    }

    @GetMapping("/loginSuccess")
    public String loginComplete(@SocialUser User user) {
        return "redirect:/board";
    }
}
