package com.example.timerms.model.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TimeNoteIdRequestDto {
    Long timeNoteId;
    Long timerId;
}
