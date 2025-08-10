package com.example.planms.model.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StatisticsResponseDto {
    int finishedPercentage;
    int almostFinishedPercentage;
    int didntPercentage;
    int noStatusPercentage;
    List<Integer> activeDays;
}
