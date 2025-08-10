package com.example.timerms.model.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class KafkaRequest {
    String username;
    Long timeNoteId;
    String note;
    LocalDate planDate;
    Long containerIndex;
}
