package com.example.planms.service;

import com.example.planms.model.dto.request.*;
import com.example.planms.model.dto.response.PlanResponseDto;
import com.example.planms.model.dto.response.connect_goal.GoalResponseDtoForConnect;
import com.example.planms.model.entity.Plan;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IPlanService {

    ResponseEntity<String> savePlan(PlanRequestDto planRequestDto,String jwtToken);

    ResponseEntity<Plan> getPlanByUsernameAndDate(String planDate, String jwtToken);

    ResponseEntity<String> updatePlanStatusRequest(String jwtToken, PlanStatusUpdateRequestDto planStatusRequestDto);

    ResponseEntity<String> updatePlan(Plan plan,PlanRequestDto planRequestDto,String username);
    void updatePlanStatus(KafkaRequest kafkaRequest);
    ResponseEntity<String> connectPlanToGoal(PlanConnectRequestDto requestDto, String jwtToken);
    void connectPlanItemToGoalPlanItem(KafkaRequest request);
    void connectPlanItemToGoalPlanItemKafkaRequest(PlanItemConnectRequestDto requestDto, String jwtToken);

    void disconnectPlanItemToGoalPlanItem(KafkaRequest request);
    void disconnectPlanItemToGoalPlanItemKafkaRequest(PlanItemConnectRequestDto requestDto, String jwtToken);

    ResponseEntity<GoalResponseDtoForConnect> getGoal(Long goalId, String jwtToken);

    List<Plan> getLast20DaysPlans(String jwtToken);

    ResponseEntity<?> updateNote(PlanNoteUpdateRequest request, String username);

    ResponseEntity<String> disconnectPlanToGoal(DisconnectGoalRequestDto requestDto, String username);

    void deletePlanTimeNoteConnection(KafkaRequest request);
}
