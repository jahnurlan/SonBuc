package com.example.workspacems.util;

import com.example.commonsecurity.auth.services.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;


@RequiredArgsConstructor
@Slf4j
@Configuration
public class Helper {
    private final JwtService jwtService;

    public String getUsername(String jwtToken) {
        String jwt = jwtToken.substring(7);
        return jwtService.extractUsername(jwt);
    }
}
