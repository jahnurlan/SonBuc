package com.example.adminms.service;

import com.example.adminms.model.response.PlanStatisticsResponseDto;
import org.springframework.http.ResponseEntity;

public interface IPlanService {
    ResponseEntity<PlanStatisticsResponseDto> getUserStatistics();
}
