package com.example.workspacems.model.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StatisticsResponseDto {
    long countAllWorkspaces;
    long countTodayAllWorkspaces;
}
