package com.example.workspacems.service;

import com.example.workspacems.model.dto.response.StatisticsResponseDto;
import org.springframework.http.ResponseEntity;

public interface IAdminService {
    ResponseEntity<StatisticsResponseDto> getUserStatistics();
}
