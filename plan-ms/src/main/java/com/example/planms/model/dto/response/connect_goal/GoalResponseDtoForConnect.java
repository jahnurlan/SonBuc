package com.example.planms.model.dto.response.connect_goal;

import com.example.planms.model.entity.GoalItem;
import com.example.planms.model.enums.GoalType;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GoalResponseDtoForConnect {
    Long id;
    LocalDate date;
    String username;
    GoalType goalType;

    List<GoalContainerResponseDto> goalContainers;
    GoalItem goalItem;

    List<ConnectPlanResponseDto> plans;
}






