package com.example.planms.service;

import com.example.planms.model.dto.request.PlanRequestDto;
import com.example.planms.model.dto.response.PlanContainerResponseDto;
import com.example.planms.model.dto.response.SharedPlanResponseDto;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ISharedPlanService {
    ResponseEntity<List<SharedPlanResponseDto>> getSharedPlanHistory(String friendUsername, String jwtToken);

    ResponseEntity<List<PlanContainerResponseDto>> getUpdatedPlanItems(String secondUsername, PlanRequestDto requestDto, String jwtToken);
}
