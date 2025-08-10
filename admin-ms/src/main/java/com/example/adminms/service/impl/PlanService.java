package com.example.adminms.service.impl;

import com.example.adminms.feigns.PlanServiceFeignClient;
import com.example.adminms.model.response.PlanStatisticsResponseDto;
import com.example.adminms.service.IPlanService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PlanService implements IPlanService {
    private final PlanServiceFeignClient planServiceFeignClient;

    @Override
    public ResponseEntity<PlanStatisticsResponseDto> getUserStatistics() {
        return ResponseEntity.ok(planServiceFeignClient.getUserStatistics());
    }
}
