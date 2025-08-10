package com.example.workspacems.config;

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
    private final RateLimitingFilter rateLimitingFilter;

    private static final String POST_GET_WORKSPACE = "/crud/**";
    private static final String POST_GET_ADMIN = "/admin/**";

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests(authorizeRequests ->
                        authorizeRequests
                                .antMatchers(POST_GET_ADMIN).hasRole(RoleType.ADMIN.name())
                                .antMatchers(POST_GET_WORKSPACE).hasAnyRole(RoleType.USER.name(), RoleType.GUEST.name())
                )
                .addFilterBefore(rateLimitingFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
    }
}
