package com.example.planms.model.dto.response;

import com.example.planms.model.entity.PlanContainer;
import com.example.planms.model.entity.PlanItem;
import com.example.planms.model.enums.GoalType;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SharedPlanResponseDto {
    Long id;
    String username;
    LocalDate planDate;
    List<PlanContainer> planContainerList;
    Long goalId;
    GoalType goalType;

    Set<PlanDTO> connectedPlans;
}






