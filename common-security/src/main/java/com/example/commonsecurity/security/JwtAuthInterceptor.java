package com.example.commonsecurity.security;

import com.example.commonsecurity.auth.SecurityHelper;
import com.example.commonsecurity.auth.services.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtAuthInterceptor implements ChannelInterceptor {
    private final SecurityHelper securityHelper;
    private final JwtService jwtService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        log.info("JwtAuthInterceptor method working");

        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        String token = accessor.getFirstNativeHeader("Authorization");

        try {
            log.info("Gonderilen token => {}", token);

            if (token != null && securityHelper.validateToken(token.replace("Bearer ", ""))) {
                String username = jwtService.extractUsername(token.replace("Bearer ", ""));
                log.info("Edited token username => {}", username);
                accessor.getSessionAttributes().put("username", username); // Session’a kullanıcı adını ekleyin
            } else {
                log.warn("JWT token eksik veya geçersiz!");
            }
        } catch (IllegalArgumentException e) {
            log.error("JWT token doğrulaması sırasında hata oluştu: {}", e.getMessage(), e);
        } catch (Exception e) {
            log.error("Bir hata oluştu: {}", e.getMessage(), e);
        }

        return message;
    }

}
