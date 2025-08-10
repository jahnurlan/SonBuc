package com.example.workspacems.model.dto.request;

import com.example.workspacems.model.enums.ScheduledType;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalTime;
import java.util.List;

@Data
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WorkspaceRequestDto {
    Long id;
    String title;
    String description;
    ScheduledType scheduleType;

    LocalTime startTime;
    LocalTime endTime;

    List<DayRequestDto> dayList;
    List<TaskRequestDto> taskRequestDtoList;
}
