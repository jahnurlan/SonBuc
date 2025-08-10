package com.example.userms.config;

import com.example.commonsecurity.config.ApplicationSecurityConfigurer;
import com.example.commonsecurity.model.RoleType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Slf4j
@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
@ComponentScan("com.example.commonsecurity")
public class SecurityConfig implements ApplicationSecurityConfigurer {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final RateLimitingFilter rateLimitingFilter;
    private final AuthenticationProvider authenticationProvider;

    private static final String POST_GET_USER = "/auth/**";
    private static final String POST_GET_USER_INFO = "/info";
    private static final String POST_GET_ADMIN = "/admin/**";

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
                .authenticationProvider(authenticationProvider)
                .authorizeRequests(authorizeRequests ->
                        authorizeRequests
                                .antMatchers(POST_GET_ADMIN).hasRole(RoleType.ADMIN.name())
                                .antMatchers(POST_GET_USER).permitAll()
                                .antMatchers(POST_GET_USER_INFO).authenticated()
                )
                .addFilterBefore(rateLimitingFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
    }
}
