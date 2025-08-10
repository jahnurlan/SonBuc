package com.example.planms.model.dto.request;

import com.example.planms.model.enums.GoalType;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class KafkaContainerNameUpdateRequest {
    LocalDate planDate;
    int containerIndex;
    String newName;
    GoalType goalType;


}

