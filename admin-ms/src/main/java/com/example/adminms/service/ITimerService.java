package com.example.adminms.service;

import com.example.adminms.model.response.TimeNoteStatisticsResponseDto;
import org.springframework.http.ResponseEntity;

public interface ITimerService {
    ResponseEntity<TimeNoteStatisticsResponseDto> getUserStatistics();
}
