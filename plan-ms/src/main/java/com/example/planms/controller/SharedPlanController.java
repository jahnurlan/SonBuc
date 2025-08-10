package com.example.planms.controller;


import com.example.planms.model.dto.request.PlanRequestDto;
import com.example.planms.model.dto.response.PlanContainerResponseDto;
import com.example.planms.model.dto.response.SharedPlanResponseDto;
import com.example.planms.service.ISharedPlanService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/sh-plan")
public class SharedPlanController {
    private final ISharedPlanService sharedPlanService;

    @GetMapping("/history/{friend-username}")
    public ResponseEntity<List<SharedPlanResponseDto>> getSharedPlanHistoryByUsernameAndDate(@PathVariable(name = "friend-username") String friendUsername, @RequestHeader(HttpHeaders.AUTHORIZATION) String jwtToken){
        return sharedPlanService.getSharedPlanHistory(friendUsername,jwtToken);
    }

    @PutMapping("/updated-plan")
    public ResponseEntity<List<PlanContainerResponseDto>> getUpdatedPlanItemsFromSharedPlan(
            @RequestParam(name = "secondUsername") String secondUsername,
            @RequestBody PlanRequestDto requestDto,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String jwtToken){

        return sharedPlanService.getUpdatedPlanItems(secondUsername,requestDto,jwtToken);
    }
}

