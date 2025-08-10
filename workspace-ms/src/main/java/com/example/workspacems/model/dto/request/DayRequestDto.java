package com.example.workspacems.model.dto.request;

import com.example.workspacems.model.enums.ScheduledType;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DayRequestDto {
    LocalDate day;

    String description;
    ScheduledType scheduleType;

    LocalTime startTime;
    LocalTime endTime;

    List<TaskRequestDto> taskRequestDtoList;
}
