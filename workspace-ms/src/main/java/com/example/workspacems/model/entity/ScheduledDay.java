package com.example.workspacems.model.entity;

import com.example.workspacems.model.enums.DayStatusType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ScheduledDay {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    LocalDate day;
    String description;

    LocalTime startTime;
    LocalTime endTime;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    DayStatusType status = DayStatusType.DEFAULT;

    @Builder.Default
    @OneToMany(mappedBy = "scheduledDay", cascade = CascadeType.ALL, orphanRemoval = true)
    List<ScheduledDayTask> tasks = new ArrayList<>();

    @ManyToOne
    @JsonIgnore
    private Workspace workspace;
}
