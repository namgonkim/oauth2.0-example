package com.gonne.oauth.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
/**
 * Created by zxcv9455@naver.com on 2021-02-09
 * Blog : http://velog.io/@namgonkim
 * Github : http://github.com/namgonkim
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception{

        // Endpoint의 접근 제한을 커스터마이징 한다.
        httpSecurity.authorizeRequests()
                .antMatchers("/", "/oauth2/**", "/login/**", "/images/**", "/js/**", "/console/**", "/favicon.ico/**")
                .permitAll()
                // 위에 매칭된 페이지(엔드포인트)들은 별다른 권한 없이 Anonymous로 접근할 수 있도록 함. -> permitAll()
                .anyRequest().authenticated()
                // 나머지 엔드포인트에 대해서는 인증되어야만 사용할 수 있도록 authenticated() 메소드를 사용
                .and()
                .oauth2Login()
                // oauth2 로그인을 진행하기 위한 메소드를 사용해 로그인 코드를 가져옴
                .defaultSuccessUrl("/loginSuccess").failureUrl("/loginFailure")
                // 로그인 성공과 실패 시의 엔드포인트를 잡아줌
                .and()
                .headers().frameOptions().disable()
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/login"))
                // 인증이 진행되지 않은 상태에서 페이지에 접근할 경우 자동으로 login 페이지로 리다이렉트 되도록 함
                .and()
                .formLogin().successForwardUrl("/board")
                // defaultSuccessUrl과 successForwardUrl을 같이쓰는 이유를 모르겠음.
                // 둘다 로그인 성공시 이동할 페이지를 말하는 것 같은데..
                .and()
                .logout().logoutUrl("/logout").logoutSuccessUrl("/")
                .deleteCookies("JSESSIONID").invalidateHttpSession(true)
                // 로그아웃 시 성공하면 홈으로 이동하고, 세션 종료와 쿠키 삭제
                .and()
                .csrf().disable();
                // csrf 옵션 disable


    }
}
