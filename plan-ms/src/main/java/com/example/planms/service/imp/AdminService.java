package com.example.planms.service.imp;

import com.example.planms.model.dto.response.PlanStatisticsResponseDto;
import com.example.planms.repository.goal.GoalRepository;
import com.example.planms.repository.plan.PlanRepository;
import com.example.planms.service.IAdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminService implements IAdminService {
    private final PlanRepository planRepository;
    private final GoalRepository goalRepository;

    @Override
    public ResponseEntity<PlanStatisticsResponseDto> getUserStatistics() {
        long allPlansCount = planRepository.countAllPlans();
        long allTodayPlansCount = planRepository.countPlansByDate(LocalDate.now());

        long allConnectedPlansCount = planRepository.countPlansWithGoal();
        long allTodayConnectedPlansCount = planRepository.countTodayPlansWithGoal(LocalDate.now());

        long allGoalsCount = goalRepository.countAllGoals();
        long allTodayGoalsCount = goalRepository.countGoalsByDate(LocalDate.now());

        PlanStatisticsResponseDto statisticsResponseDto = PlanStatisticsResponseDto.builder()
                .allPlansCount(allPlansCount)
                .allTodayPlansCount(allTodayPlansCount)
                .allConnectedPlansCount(allConnectedPlansCount)
                .allTodayConnectedPlansCount(allTodayConnectedPlansCount)
                .allGoalsCount(allGoalsCount)
                .allTodayGoalsCount(allTodayGoalsCount)
                .build();
        return ResponseEntity.ok(statisticsResponseDto);
    }
}
