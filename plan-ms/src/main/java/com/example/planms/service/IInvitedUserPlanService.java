package com.example.planms.service;

import com.example.planms.model.dto.request.PlanRequestDto;
import org.springframework.http.ResponseEntity;

public interface IInvitedUserPlanService {
    ResponseEntity<String> savePlan(PlanRequestDto planRequestDto, String jwtToken);
}
