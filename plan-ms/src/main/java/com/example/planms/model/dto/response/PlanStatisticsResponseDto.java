package com.example.planms.model.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PlanStatisticsResponseDto {
    long allPlansCount;
    long allTodayPlansCount;
    long allConnectedPlansCount;
    long allTodayConnectedPlansCount;
    long allGoalsCount;
    long allTodayGoalsCount;
}
