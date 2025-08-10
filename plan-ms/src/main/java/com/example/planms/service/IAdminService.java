package com.example.planms.service;


import com.example.planms.model.dto.response.PlanStatisticsResponseDto;
import org.springframework.http.ResponseEntity;

public interface IAdminService {
    ResponseEntity<PlanStatisticsResponseDto> getUserStatistics();
}
