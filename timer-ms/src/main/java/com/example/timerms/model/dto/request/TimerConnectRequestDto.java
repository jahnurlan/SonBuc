package com.example.timerms.model.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TimerConnectRequestDto {
    Long timerNoteId;
    Long planContainerIndex;
    LocalDate planDate;

}
