package com.example.planms.controller;

import com.example.planms.model.dto.response.PlanStatisticsResponseDto;
import com.example.planms.service.IAdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {
    private final IAdminService adminService;

    @GetMapping("/info")
    public ResponseEntity<PlanStatisticsResponseDto> getUserStatistics(){
        return adminService.getUserStatistics();
    }
}
