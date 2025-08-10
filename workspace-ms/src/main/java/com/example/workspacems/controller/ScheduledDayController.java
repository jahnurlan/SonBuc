package com.example.workspacems.controller;

import com.example.workspacems.model.dto.request.TaskRequestDto;
import com.example.workspacems.service.IScheduledDayService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/sd")
public class ScheduledDayController {
    private final IScheduledDayService scheduledDayService;

    @PostMapping("/save")
    public ResponseEntity<String> saveScheduledDays(
            @RequestBody TaskRequestDto scheduledDayRequestDto,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String jwtToken
    ){
        return scheduledDayService.saveScheduledDay(scheduledDayRequestDto, jwtToken);
    }
}
