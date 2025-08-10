package com.example.workspacems.controller;

import com.example.workspacems.model.dto.response.StatisticsResponseDto;
import com.example.workspacems.service.IAdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {
    private final IAdminService adminService;

    @GetMapping("/info")
    public ResponseEntity<StatisticsResponseDto> getUserStatistics(Principal principal){
        return adminService.getUserStatistics();
    }
}
