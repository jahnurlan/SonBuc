package com.example.adminms.model.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WorkspaceStatisticsResponseDto {
    long countAllWorkspaces;
    long countTodayAllWorkspaces;
}
