package com.example.planms.model.dto.request;

import com.example.planms.model.entity.PlanContainer;
import com.example.planms.model.enums.GoalType;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class KafkaRequest {
    String username;
    String firstUsername;
    GoalType goalType;
    String planType;
    Long planContainerId;
    Long timeNoteId;

    int planItemIndex;
    String planItemText;

    int containerIndex;
    String containerName;

    String status;
    LocalDate goalDate;
    LocalDate planDate;
    LocalDateTime planDateTime;
    List<Long> ids;
    String firstUser;
    String secondUser;
    List<PlanItemRequestDto> planItemList;
    List<PlanContainer> planContainerList;

    Long planId;
    Long goalId;
}
