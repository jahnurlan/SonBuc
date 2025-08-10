package com.example.planms.controller;

import com.example.planms.model.dto.response.RankResponseDto;
import com.example.planms.model.dto.response.StatisticsResponseDto;
import com.example.planms.service.IRankService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/rank")
public class RankController {
    private final IRankService rankService;


    @GetMapping("/statistics")
    public ResponseEntity<StatisticsResponseDto> getStatisticsByUsername(@RequestHeader(HttpHeaders.AUTHORIZATION) String jwtToken){
        return rankService.getStatisticsByUsername(jwtToken);
    }

    @GetMapping("/level")
    public ResponseEntity<RankResponseDto> getRankByUsername(@RequestHeader(HttpHeaders.AUTHORIZATION) String jwtToken){
        return rankService.getRankByUsername(jwtToken);
    }
}
