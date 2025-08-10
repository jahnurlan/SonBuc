package com.example.timerms.model.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TimerRequestDto {
    OffsetDateTime startTime;
    OffsetDateTime stopTime;

    LocalDate day;
    Long timeNoteId;
}
