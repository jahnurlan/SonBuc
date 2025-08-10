package com.example.commonsecurity.auth;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.Jwts;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
public class SecurityHelper {
    @Value("${application.security.secret-key}")
    String secretKey;

    public boolean validateToken(String token) {
        try {
            // Token'ın imzasını ve geçerliliğini doğrular
            Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(token);
            return true; // Token geçerliyse true döner
        } catch (SignatureException ex) {
            System.out.println("Invalid JWT signature");
        } catch (ExpiredJwtException ex) {
            System.out.println("Expired JWT token");
        } catch (Exception ex) {
            System.out.println("Invalid JWT token");
        }
        return false; // Token geçersizse false döner
    }
    public boolean servletPathIsAuth(@NonNull HttpServletRequest httpServletRequest) {
        return httpServletRequest.getServletPath().contains("/auth");
    }

    public boolean authHeaderIsValid(String authorizationHeader) {
        return authorizationHeader != null && authorizationHeader.startsWith("Bearer ");
    }

    public boolean isJwtUsedFirst(String username) {
        return username != null && SecurityContextHolder.getContext().getAuthentication() == null;
    }


}