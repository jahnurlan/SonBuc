package com.example.userms.service.impl;

import com.example.commonexception.exceptions.ResourceNotFoundException;
import com.example.commonsecurity.auth.services.JwtService;
import com.example.userms.model.dto.request.AdminLoginRequestDto;
import com.example.userms.model.dto.response.AuthenticationResponse;
import com.example.userms.model.dto.response.StatisticsResponseDto;
import com.example.userms.model.entity.Role;
import com.example.userms.model.entity.User;
import com.example.userms.model.enums.RoleType;
import com.example.userms.repository.RoleRepository;
import com.example.userms.repository.UserRepository;
import com.example.userms.service.IAdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminService implements IAdminService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final JwtService jwtService;

    @Override
    public ResponseEntity<StatisticsResponseDto> getUserStatistics() {
        long allUserCount = userRepository.countUsersByRole(RoleType.USER);
        long allGuestUserCount = userRepository.countUsersByRole(RoleType.GUEST);

        long todayRegisteredUserCount = userRepository.countTodayUsersWithUserRoleNative(RoleType.USER.name());
        long todayRegisteredGuestUserCount = userRepository.countTodayUsersWithUserRoleNative(RoleType.GUEST.name());

        long inActiveUserCount = userRepository.countInactiveUsers();

        StatisticsResponseDto statisticsResponseDto = StatisticsResponseDto.builder()
                .allUserCount(allUserCount)
                .allGuestUserCount(allGuestUserCount)
                .allTodayRegisteredUserCount(todayRegisteredUserCount)
                .allTodayRegisteredGuestUserCount(todayRegisteredGuestUserCount)
                .inActiveUserCount(inActiveUserCount)
                .build();
        return ResponseEntity.ok().body(statisticsResponseDto);
    }

    @Override
    public ResponseEntity<AuthenticationResponse> generateAdminToken(AdminLoginRequestDto adminLoginRequestDto) {
        if (adminLoginRequestDto.getUsername().equals("aylan") && adminLoginRequestDto.getPassword().equals("12345678912345")){
            Role role = roleRepository.findRoleByName(RoleType.ADMIN)
                    .orElseThrow(() -> new ResourceNotFoundException("ROLE_NOT_FOUND"));

            String guestUsername = "admin_" + UUID.randomUUID().toString().substring(0, 8);
            User guestUser = User.builder()
                    .username(guestUsername)
                    .role(role) // Enum: GUEST, USER, ADMIN
                    .enabled(true)
                    .build();
            log.info("Guest Admin created => {}", guestUser);

            String accessToken = jwtService.generateGuestAccessToken(guestUser);
            log.info("Guest Admin access token created ==> {}", accessToken);

            return ResponseEntity.ok(AuthenticationResponse.builder()
                    .accessToken(accessToken)
                    .build());
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }
}
