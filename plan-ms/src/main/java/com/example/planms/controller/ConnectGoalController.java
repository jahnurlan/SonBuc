package com.example.planms.controller;

import com.example.planms.model.dto.request.DisconnectGoalRequestDto;
import com.example.planms.model.dto.request.PlanConnectRequestDto;
import com.example.planms.model.dto.request.PlanItemConnectRequestDto;
import com.example.planms.model.dto.response.connect_goal.GoalResponseDtoForConnect;
import com.example.planms.model.entity.Goal;
import com.example.planms.service.IGoalService;
import com.example.planms.service.IPlanService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/connect")
public class ConnectGoalController {
    private final IPlanService planService;
    private final IGoalService goalService;

    @PostMapping("/connect-goal")
     public ResponseEntity<String> connectPlanToGoal(@Valid @RequestBody PlanConnectRequestDto planConnectRequestDto, @RequestHeader(name = "Authorization") String jwtToken){
       System.out.println("6344 controller planConnectRequestDto => " + planConnectRequestDto);
        return planService.connectPlanToGoal(planConnectRequestDto,jwtToken);
    }

    @PostMapping("/disconnect-goal")
    public ResponseEntity<String> disconnectPlanToGoal(@Valid @RequestBody DisconnectGoalRequestDto requestDto, Principal principal){
        System.out.println("6344 controller disconnectPlanToGoal => " + requestDto);
        return planService.disconnectPlanToGoal(requestDto,principal.getName());
    }

    @PostMapping("/connect-goal-plan-item")
    public void connectPlanItemToGoalPlanItem(@Valid @RequestBody PlanItemConnectRequestDto planItemConnectRequestDto, @RequestHeader(name = "Authorization") String jwtToken){
        System.out.println("6344 controller planConnectRequestDto => " + planItemConnectRequestDto);
        planService.connectPlanItemToGoalPlanItemKafkaRequest(planItemConnectRequestDto,jwtToken);
    }

    @PostMapping("/disconnect-goal-plan-item")
    public void disconnectPlanItemToGoalPlanItem(@Valid @RequestBody PlanItemConnectRequestDto planItemConnectRequestDto, @RequestHeader(name = "Authorization") String jwtToken){
        System.out.println("6344 controller planConnectRequestDto => " + planItemConnectRequestDto);
        planService.disconnectPlanItemToGoalPlanItemKafkaRequest(planItemConnectRequestDto,jwtToken);
    }

    @GetMapping("/get-goal/{id}")
    public ResponseEntity<GoalResponseDtoForConnect> getGoal(@PathVariable(name = "id") Long goalId, @RequestHeader(name = "Authorization") String jwtToken){
        //TODO Goal`ı db üzərindən tap onun GoalPlanİtem siyahısını Planİd üçün response elə.Response`da hər goalPlanİtem`ın Planİtem siyahısını görə bilək
        return planService.getGoal(goalId, jwtToken);
    }

    @PostMapping("/delete")
    public ResponseEntity<String> deleteGoalConnectionByIdAndPlanDate(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate planDate, @RequestHeader(name = HttpHeaders.AUTHORIZATION) String jwtToken){
        return goalService.deleteGoalPlanConnection(planDate, jwtToken);
    }
}
