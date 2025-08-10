package com.example.userms.controller;

import com.example.userms.model.dto.response.StatisticsResponseDto;
import com.example.userms.model.dto.response.UserResponseDto;
import com.example.userms.service.IAdminService;
import com.example.userms.service.IGuestService;
import com.example.userms.service.IUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
