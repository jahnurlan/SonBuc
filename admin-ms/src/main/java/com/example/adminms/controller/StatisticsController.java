package com.example.adminms.controller;

import com.example.adminms.model.response.PlanStatisticsResponseDto;
import com.example.adminms.model.response.TimeNoteStatisticsResponseDto;
import com.example.adminms.model.response.UserStatisticsResponseDto;
import com.example.adminms.model.response.WorkspaceStatisticsResponseDto;
import com.example.adminms.service.IPlanService;
import com.example.adminms.service.ITimerService;
import com.example.adminms.service.IUserService;
import com.example.adminms.service.IWorkspaceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/statistics")
public class StatisticsController {
    private final IUserService userService;
    private final IPlanService planService;
    private final ITimerService timerService;
    private final IWorkspaceService workspaceService;

    @GetMapping("/user")
    public ResponseEntity<UserStatisticsResponseDto> getUserStatistics(){
        return userService.getUserStatistics();
    }

    @GetMapping("/plan")
    public ResponseEntity<PlanStatisticsResponseDto> getPlanStatistics(){
        return planService.getUserStatistics();
    }

    @GetMapping("/timer")
    public ResponseEntity<TimeNoteStatisticsResponseDto> getTimerStatistics(){
        return timerService.getUserStatistics();
    }

    @GetMapping("/workspace")
    public ResponseEntity<WorkspaceStatisticsResponseDto> getWorkspaceStatistics(){
        return workspaceService.getUserStatistics();
    }

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Rate Limiting test passed");
    }
}