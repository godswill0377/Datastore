package com.dataexo.zblog.auth;

import com.dataexo.zblog.service.UserService;
import com.dataexo.zblog.util.Md5Util;
import com.dataexo.zblog.vo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.*;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


@Configuration
@EnableWebSecurity
public class CustomAuthenticationProvider implements AuthenticationProvider {
    @Autowired
    private UserService userService;
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) authentication;
        String username = token.getName();

        User user = null;
        if (username != null) {
            user = userService.loadUserByUsername(username);
        }
        //
        if (user == null) {
            throw new UsernameNotFoundException("This username doesn't found.");
        }

        //数据库用户的密码
        String password = user.getPassword();
        String pwdDigest = Md5Util.pwdDigest(token.getCredentials().toString());
        //与authentication里面的credentials相比较
        if (!password.equals(pwdDigest)) {

            throw new BadCredentialsException("Invalid username/password");
        }
        //授权
        return new UsernamePasswordAuthenticationToken(user, password, user.getAuthorities());
    }

    public void config(WebSecurity web) {
        web.ignoring().antMatchers("/js/**", "/css/**", "/vendor/**", "/image/**", "/admin/**");
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.equals(authentication);
    }
}


