package com.example.timerms.model.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;

@Entity
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TimeNote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    LocalDate day;

    OffsetDateTime creatingTime;
    LocalTime duration;

    OffsetDateTime startTime;
    OffsetDateTime stopTime;

    String timerStatus;
    String username;

    String note;
    Long planId;
    Long containerIndex;
}
