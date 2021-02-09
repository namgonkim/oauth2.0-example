package com.gonne.oauth.repository;

import com.gonne.oauth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
/**
 * Created by zxcv9455@naver.com on 2021-02-09
 * Blog : http://velog.io/@namgonkim
 * Github : http://github.com/namgonkim
 */
public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
}
