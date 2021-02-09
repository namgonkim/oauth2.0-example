package com.gonne.oauth.resolver;

import com.gonne.oauth.annotation.SocialUser;
import com.gonne.oauth.entity.SocialType;
import com.gonne.oauth.entity.User;
import com.gonne.oauth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.Map;

import static com.gonne.oauth.entity.SocialType.GOOGLE;


/**
 * Created by zxcv9455@naver.com on 2021-02-09
 * Blog : http://velog.io/@namgonkim
 * Github : http://github.com/namgonkim
 * 소셜 유저로 로그인한 뒤, 세션에서 정보를 가지고 옴.
 * 기존 사용자가 해당 이메일 주소를 가지고 가입을 했다면 유저와 소셜 로그인 할 수 있도록 연동되고
 * 그렇지 않은 경우 새로운 사용자를 만드는 방향으로 구현
 */

@RequiredArgsConstructor
@Component
public class UserArgumentResolver implements HandlerMethodArgumentResolver {
    private final UserRepository userRepository;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterAnnotation(SocialUser.class) != null &&
                parameter.getParameterType().equals(User.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        // RequestContextHolder: 스프링2.x부터 제공되는 기능 중 하나로 전 로직에서 HttpServletRequest에 접근할 수 있도록 도와줌.
        // 주로 세션 스코프에 있는 정보를 사용할 때 이용
        HttpSession session = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest().getSession();
        User user = (User) session.getAttribute("user");
        return getUser(user, session);
    }

    private User getUser(User user, HttpSession session){
        if(user == null) {
            try {
                // SecurityContextHolder: 스프링 시큐리티의 핵심 기술 Authentication(인증)을 보관하는 곳
                // 인증된 사용자의 정보를 보관하고 있다.
                // OAuth2AuthenticationToken: 인증된 사용자의 엑세스 토큰을 SecurityContextHolder에서 가져와 보관하도록 함.
                OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
                Map<String, Object> map = token.getPrincipal().getAttributes();

                // 소셜에서 아이디 정보를 가져온다
                User convertUser = convertUser(token.getAuthorizedClientRegistrationId(), map);

                // 가져온 인증 정보를 레파지토리에서 찾는다. 없으면 레파지토리에 저장
                user = userRepository.findByEmail(convertUser.getEmail());
                if(user == null) user = userRepository.save(convertUser);

                // 권한 설정하는 부분
                setRoleIfNotSame(user, token, map);
                session.setAttribute("user", user);

            }
            catch (ClassCastException e) {
                return user;
            }
        }

        return user;
    }

    // GOOGLE에 저장된 인증 정보를 MAP타입으로 변환하고 저장
    private User convertUser(String authority, Map<String, Object> map) {
        if (GOOGLE.getValue().equals(authority))
            return getModernUser(GOOGLE, map);
        return null;
    }

    // 저장
    private User getModernUser(SocialType socialType, Map<String, Object> map) {
        return User.builder()
                .name(String.valueOf(map.get("name")))
                .email(String.valueOf(map.get("email")))
                .principal(String.valueOf(map.get("id"))) // principal이 id
                .socialType(socialType)
                .createdDate(LocalDateTime.now())
                .updatedDate(LocalDateTime.now())
                .build();
    }

    private void setRoleIfNotSame(User user, OAuth2AuthenticationToken token, Map<String, Object> map) {
        if(!token.getAuthorities().contains(
                new SimpleGrantedAuthority(user.getSocialType().getRoleType()))) {
            SecurityContextHolder.getContext().setAuthentication(
                    new UsernamePasswordAuthenticationToken(map, "N/A",
                            AuthorityUtils.createAuthorityList(user.getSocialType().getRoleType())));
        }
    }
}
