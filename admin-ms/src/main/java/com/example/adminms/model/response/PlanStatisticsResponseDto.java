package com.example.adminms.model.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

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
