package com.example.workspacems.model.entity;

import com.example.workspacems.model.enums.ScheduledType;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Workspace {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String username;
    String title;
    String description;

    LocalTime startTime;
    LocalTime endTime;

    LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    // Choices: Daily, Weekly, Monthly
    @Enumerated(EnumType.STRING)
    ScheduledType type;

    @Builder.Default
    boolean status = true;

    @Builder.Default
    @OneToMany(mappedBy = "workspace", cascade = CascadeType.ALL, orphanRemoval = true)
    List<ScheduledDay> scheduledDays = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "workspace", cascade = CascadeType.ALL, orphanRemoval = true)
    List<WorkspaceTask> tasks = new ArrayList<>();
}
