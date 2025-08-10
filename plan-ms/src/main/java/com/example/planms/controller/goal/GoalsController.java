package com.example.planms.controller.goal;

import com.example.planms.model.dto.request.GoalsRequestDto;
import com.example.planms.model.dto.request.PlanStatusUpdateRequestDto;
import com.example.planms.model.dto.response.connect_goal.GoalResponseDtoForConnect;
import com.example.planms.model.entity.Goal;
import com.example.planms.model.enums.GoalType;
import com.example.planms.service.IGoalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/crud")
public class GoalsController {
    private final IGoalService goalService;

    @PostMapping("/save/goal")
    public ResponseEntity<Map<String, Object>> saveGoal(@Valid @RequestBody GoalsRequestDto goalsRequestDto, @RequestHeader(name = "Authorization") String jwtToken){
        return goalService.saveGoals(goalsRequestDto,jwtToken);
    }

    @GetMapping("/goals")
    public ResponseEntity<Goal> getGoalByUsernameAndDate(
            @RequestParam @NotNull String planDate,
            @RequestParam(required = false) GoalType type,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String jwtToken
    ){
        return goalService.getGoalByUsernameAndDateAndType(planDate,type,jwtToken);
    }

    @GetMapping("/all-goals")
    public ResponseEntity<List<GoalResponseDtoForConnect>> getAllGoalsByUsername(@RequestHeader(HttpHeaders.AUTHORIZATION) String jwtToken){
        return goalService.getGoalsByUsername(jwtToken);
    }

    @PutMapping("/goals/updateStatus")
    public ResponseEntity<String> updateGoalsStatus(@RequestHeader(HttpHeaders.AUTHORIZATION) String jwtToken,@Valid @RequestBody PlanStatusUpdateRequestDto planStatusRequestDto){
        return goalService.updateGoalPlanStatusRequest(jwtToken,planStatusRequestDto);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteGoal(Principal principal, @PathVariable(name = "id") Long goalId){
        return goalService.deleteGoal(principal.getName(), goalId);
    }
    @GetMapping("/active")
    public List<Goal> getActiveGoals(Principal principal) {
        return goalService.getActiveGoals(principal.getName());
    }
}