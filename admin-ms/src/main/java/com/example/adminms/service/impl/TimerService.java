package com.example.adminms.service.impl;

import com.example.adminms.feigns.TimerServiceFeignClient;
import com.example.adminms.model.response.TimeNoteStatisticsResponseDto;
import com.example.adminms.service.IPlanService;
import com.example.adminms.service.ITimerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TimerService implements ITimerService {
    private final TimerServiceFeignClient timerServiceFeignClient;

    @Override
    public ResponseEntity<TimeNoteStatisticsResponseDto> getUserStatistics() {
        return ResponseEntity.ok(timerServiceFeignClient.getUserStatistics());
    }
}
