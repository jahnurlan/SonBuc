package com.example.planms.service;

import com.example.planms.model.dto.request.GoalsRequestDto;
import com.example.planms.model.dto.request.KafkaRequest;
import com.example.planms.model.dto.request.PlanStatusUpdateRequestDto;
import com.example.planms.model.dto.response.connect_goal.GoalResponseDtoForConnect;
import com.example.planms.model.entity.Goal;
import com.example.planms.model.enums.GoalType;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface IGoalService {
    ResponseEntity<Map<String, Object>> saveGoals(GoalsRequestDto goalsRequestDto, String jwtToken);
    ResponseEntity<Goal> getGoalByUsernameAndDateAndType(String planDate, GoalType type, String jwtToken);
    ResponseEntity<List<GoalResponseDtoForConnect>> getGoalsByUsername(String jwtToken);
    ResponseEntity<String> updateGoalPlanStatusRequest(String jwtToken, PlanStatusUpdateRequestDto statusRequest);
    void updateGoalPlanStatus(KafkaRequest kafkaRequest);

    ResponseEntity<String> deleteGoalPlanConnection(LocalDate planDate, String jwtToken);

    ResponseEntity<String> deleteGoal(String username, Long id);
    List<Goal> getActiveGoals(String username);
}
