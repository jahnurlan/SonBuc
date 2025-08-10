package com.example.adminms.service;

import com.example.adminms.model.response.UserStatisticsResponseDto;
import org.springframework.http.ResponseEntity;

public interface IUserService {
    ResponseEntity<UserStatisticsResponseDto> getUserStatistics();
}
