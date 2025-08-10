package com.example.planms.mapper;

import com.example.planms.model.dto.response.*;
import com.example.planms.model.dto.response.connect_goal.*;
import com.example.planms.model.entity.*;
import com.example.planms.model.enums.PlanType;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ModelMapper {
    public PlanResponseDto planToPlanResponseDto(Plan plan, String username){
        Set<PlanDTO> connectedPlans = new HashSet<>();

        PlanResponseDto responseDto = PlanResponseDto.builder()
                .username(username)
                .type(plan.getType())
                .planContainerList(plan.getPlanContainerList())
                .status(plan.isStatus())
                .planDate(plan.getDate())
                .goalId(plan.getGoal() != null ? plan.getGoal().getId() : null)
                .build();

        if (plan.getType().equals(PlanType.SHARED)){
            for (Plan connectedPlan : plan.getConnectedPlans()){
                if (!connectedPlan.getUsername().equals(username)){
                    connectedPlans.add(PlanDTO.fromEntity(connectedPlan));
                }
            }
            responseDto.setConnectedPlans(connectedPlans);
        }
        return responseDto;
    }

    public GoalResponseDtoForConnect goalToGoalResponseDtoForConnect(Goal goal) {
        return GoalResponseDtoForConnect.builder()
                .id(goal.getId())
                .username(goal.getUsername())
                .date(goal.getDate())
                .goalType(goal.getGoalType())
                .goalItem(goal.getGoalItem())
                .goalContainers(goal.getGoalContainers().stream()
                        .map(this::mapGoalContainerToDto)
                        .collect(Collectors.toList())
                )
                .plans(goal.getPlans().stream()
                        .map(this::mapPlanDto)
                        .collect(Collectors.toList()))
                .build();
    }

    private GoalContainerResponseDto mapGoalContainerToDto(GoalContainer container) {
        return GoalContainerResponseDto.builder()
                .id(container.getId())
                .index(container.getIndex())
                .name(container.getName())
                .planItemList(container.getPlanItemList().stream()
                        .map(this::mapGoalPlanItemToDto)
                        .collect(Collectors.toList()))
                .build();
    }

    private ConnectPlanResponseDto mapPlanDto(Plan plan) {
        return ConnectPlanResponseDto.builder()
                .id(plan.getId())
                .date(plan.getDate())
                .note(plan.getNote())
                .build();
    }

    private GoalPlanItemResponseDto mapGoalPlanItemToDto(GoalPlanItem goalPlanItem) {
        return GoalPlanItemResponseDto.builder()
                .id(goalPlanItem.getId())
                .index(goalPlanItem.getIndex())
                .text(goalPlanItem.getText())
                .planItemList(goalPlanItem.getPlanItemList().stream()
                        .map(this::mapPlanItemToDto)
                        .collect(Collectors.toList()))
                .build();
    }

    private PlanItemResponseDto mapPlanItemToDto(PlanItem planItem) {
        return PlanItemResponseDto.builder()
                .id(planItem.getId())
                .build();
    }

    public SharedPlanResponseDto planToSharedPlanHistoryResponseDto(Plan plan){
        Set<PlanDTO> connectedPlans = new HashSet<>();

        SharedPlanResponseDto responseDto = SharedPlanResponseDto.builder()
                .id(plan.getId())
                .username(plan.getUsername())
                .planContainerList(plan.getPlanContainerList())
                .planDate(plan.getDate())
                .goalId(plan.getGoal() != null ? plan.getGoal().getId() : null)
                .goalType(plan.getGoal() != null ? plan.getGoal().getGoalType() : null)
                .build();

        for (Plan connectedPlan : plan.getConnectedPlans()){
            if (!connectedPlan.getUsername().equals(plan.getUsername())){
                connectedPlans.add(PlanDTO.fromEntityToShort(connectedPlan));
            }
        }
        responseDto.setConnectedPlans(connectedPlans);
        return responseDto;
    }
}



