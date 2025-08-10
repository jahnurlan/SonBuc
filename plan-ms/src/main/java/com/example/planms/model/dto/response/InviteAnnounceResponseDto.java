package com.example.planms.model.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InviteAnnounceResponseDto {
    Long id;
    LocalDateTime planDate;
    String firstUsername;
    String announceDateDifference;
    boolean readingStatus;
}

