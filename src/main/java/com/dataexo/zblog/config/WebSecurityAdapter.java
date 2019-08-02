package com.dataexo.zblog.config;

import com.dataexo.zblog.auth.CustomAuthenticationProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;


@Configuration
@EnableWebSecurity
public class WebSecurityAdapter extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.authorizeRequests().antMatchers("/","/blog/**","/login/**","/login/auth","/admin/login/auth").permitAll()
                .antMatchers("/data_sets/question/checkUser").hasRole("ADMIN")
                .and().rememberMe().tokenValiditySeconds(3600)
                .and().logout().logoutUrl("/admin/loginOut").permitAll();
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/**/*.js","/**/*.css","/**/*.htm","*.jpg","/image/**","/vendor/**","/**/*.gif","/**/*.xml");
    }


    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authenticationProvider());
    }

    @Bean
    public AuthenticationProvider authenticationProvider(){
        AuthenticationProvider authenticationProvider=new CustomAuthenticationProvider();
        return authenticationProvider;
    }
}
