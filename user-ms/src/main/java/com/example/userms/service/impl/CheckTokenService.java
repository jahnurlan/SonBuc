package com.example.userms.service.impl;

import com.example.commonsecurity.auth.SecurityHelper;
import com.example.commonsecurity.auth.services.JwtService;
import com.example.userms.service.ICheckTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CheckTokenService implements ICheckTokenService {
    private final SecurityHelper securityHelper;
    private final JwtService jwtService;

    @Override
    public ResponseEntity<String> checkAccessToken(String header){
        String token = header.substring(7);

        if (securityHelper.authHeaderIsValid(header) && !jwtService.isTokenExpired(token)){
            return ResponseEntity.ok("Token is true!");
        }else {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Token is not true!");
        }
    }
}
