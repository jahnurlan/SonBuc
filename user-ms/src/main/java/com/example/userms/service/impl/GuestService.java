package com.example.userms.service.impl;

import com.example.commonexception.exceptions.ResourceNotFoundException;
import com.example.commonsecurity.auth.services.JwtService;
import com.example.userms.model.dto.response.AuthenticationResponse;
import com.example.userms.model.entity.Role;
import com.example.userms.model.entity.User;
import com.example.userms.model.enums.RoleType;
import com.example.userms.repository.RoleRepository;
import com.example.userms.repository.UserRepository;
import com.example.userms.service.IGuestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;


@Service
@Slf4j
@RequiredArgsConstructor
public class GuestService implements IGuestService {
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthenticationResponse generateGuestToken() {
        Role role = roleRepository.findRoleByName(RoleType.GUEST)
                .orElseThrow(() -> new ResourceNotFoundException("ROLE_NOT_FOUND"));

        // 1. Random guest username
        String guestUsername = "guest_" + UUID.randomUUID().toString().substring(0, 8);

        // 2. Guest User oluştur (ister DB'ye yaz, ister yazma)
        User guestUser = User.builder()
                .username(guestUsername)
                .password(passwordEncoder.encode(UUID.randomUUID().toString())) // rastgele parola
                .role(role) // Enum: GUEST, USER, ADMIN
                .enabled(true)
                .build();
        log.info("Guest user created => {}", guestUser);
        userRepository.save(guestUser);

        String accessToken = jwtService.generateGuestAccessToken(guestUser);
        log.info("Guest access token created ==> {}", accessToken);
        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .build();
    }

}
