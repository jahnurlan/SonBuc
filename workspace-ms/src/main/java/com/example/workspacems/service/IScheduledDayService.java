package com.example.workspacems.service;

import com.example.workspacems.model.dto.request.TaskRequestDto;
import org.springframework.http.ResponseEntity;

public interface IScheduledDayService {
    ResponseEntity<String> saveScheduledDay(TaskRequestDto scheduledDayRequestDto, String jwtToken);

}
