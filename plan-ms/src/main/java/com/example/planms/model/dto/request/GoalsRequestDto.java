package com.example.planms.model.dto.request;

import com.example.planms.model.enums.GoalType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GoalsRequestDto {
    Long id;
    boolean newGoal;

    @NotNull(message = "Plan date required")
    LocalDate planDate;
    List<PlanContainerRequestDto> goalContainerRequestDtoList;

    GoalItemRequestDto goalItemRequestDto;
    GoalType type;
}
