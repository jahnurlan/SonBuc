package com.example.planms.controller.plan;

import com.example.planms.model.dto.request.PlanConnectRequestDto;
import com.example.planms.model.dto.request.PlanNoteUpdateRequest;
import com.example.planms.model.dto.request.PlanRequestDto;
import com.example.planms.model.dto.request.PlanStatusUpdateRequestDto;
import com.example.planms.model.dto.response.PlanResponseDto;
import com.example.planms.model.entity.Plan;
import com.example.planms.service.IPlanService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/crud")
public class PlanController {
    private final IPlanService planService;

    @GetMapping("/last-20-days")
    public ResponseEntity<List<Plan>> getLast20DaysPlans(@RequestHeader(HttpHeaders.AUTHORIZATION) String jwtToken) {
        List<Plan> plans = planService.getLast20DaysPlans(jwtToken);
        return ResponseEntity.ok(plans);
    }

    @PostMapping("/save")
    public ResponseEntity<String> savePlan(@Valid @RequestBody PlanRequestDto planRequestDto,@RequestHeader(name = "Authorization") String jwtToken){
        System.out.println("5711 controller planRequestDto => " + planRequestDto);
        return planService.savePlan(planRequestDto,jwtToken);
    }

    @GetMapping("/plans")
    public ResponseEntity<Plan> getPlanByUsernameAndDate(@RequestParam("planDate") String planDate, @RequestHeader(HttpHeaders.AUTHORIZATION) String jwtToken){
        return planService.getPlanByUsernameAndDate(planDate,jwtToken);
    }

    @PutMapping("/update-status")
    public ResponseEntity<String> updatePlanStatus(@RequestHeader(HttpHeaders.AUTHORIZATION) String jwtToken,@Valid @RequestBody PlanStatusUpdateRequestDto planStatusRequestDto){
        return planService.updatePlanStatusRequest(jwtToken,planStatusRequestDto);
    }

    @PostMapping("/connect-goal")
    public ResponseEntity<String> connectPlanToGoal(@Valid @RequestBody PlanConnectRequestDto planConnectRequestDto, @RequestHeader(name = "Authorization") String jwtToken){
        System.out.println("6344 controller planConnectRequestDto => " + planConnectRequestDto);
        return planService.connectPlanToGoal(planConnectRequestDto,jwtToken);
    }

    @PutMapping("/note")
    public ResponseEntity<?> updateNote(@RequestBody PlanNoteUpdateRequest request, Principal principal) {
        String username = principal.getName();
        return planService.updateNote(request, username);
    }
}
