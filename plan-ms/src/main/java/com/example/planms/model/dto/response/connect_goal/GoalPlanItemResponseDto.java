package com.example.planms.model.dto.response.connect_goal;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GoalPlanItemResponseDto {
    Long id;
    int index;
    String text;
    String username;

    List<PlanItemResponseDto> planItemList;
}






