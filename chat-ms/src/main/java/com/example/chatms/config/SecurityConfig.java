package com.example.chatms.config;

import com.example.commonsecurity.config.ApplicationSecurityConfigurer;
import com.example.commonsecurity.model.RoleType;
import com.example.commonsecurity.security.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Slf4j
@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
@ComponentScan("com.example.commonsecurity")
public class SecurityConfig implements ApplicationSecurityConfigurer {
    private final JwtAuthFilter jwtAuthenticationFilter;
    private static final String POST_GET_MESSAGES = "/msg/**";
    static final String POST_GET_WEBSOCKET = "/ws/**";
    static final String POST_GET_USER_STATUS = "/us/**";
    static final String POST_GET_EDIT_MESSAGE = "/edit/**";

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests(authorizeRequests ->
                        authorizeRequests
                                .antMatchers(POST_GET_MESSAGES).hasRole(RoleType.USER.name())
                                .antMatchers(POST_GET_WEBSOCKET).permitAll()
                                .antMatchers(POST_GET_USER_STATUS).authenticated()
                                .antMatchers(POST_GET_EDIT_MESSAGE)
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
    }
}


