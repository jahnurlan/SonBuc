package com.example.adminms.service.impl;

import com.example.adminms.feigns.UserServiceFeignClient;
import com.example.adminms.model.response.UserStatisticsResponseDto;
import com.example.adminms.service.IUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService implements IUserService {
    private final UserServiceFeignClient userServiceFeignClient;

    @Override
    public ResponseEntity<UserStatisticsResponseDto> getUserStatistics() {
        return ResponseEntity.ok(userServiceFeignClient.getUserStatistics());
    }
}
