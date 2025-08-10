package com.example.userms.service;

import com.example.userms.model.dto.request.AdminLoginRequestDto;
import com.example.userms.model.dto.response.AuthenticationResponse;
import com.example.userms.model.dto.response.StatisticsResponseDto;
import org.springframework.http.ResponseEntity;

public interface IAdminService {
    ResponseEntity<StatisticsResponseDto> getUserStatistics();

    ResponseEntity<AuthenticationResponse> generateAdminToken(AdminLoginRequestDto adminLoginRequestDto);
}
