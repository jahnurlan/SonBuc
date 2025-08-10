package com.example.timerms.service;

import com.example.timerms.model.dto.response.StatisticsResponseDto;
import org.springframework.http.ResponseEntity;

public interface IAdminService {
    ResponseEntity<StatisticsResponseDto> getUserStatistics();
}
