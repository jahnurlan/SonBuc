package com.example.planms.model.dto.request;

import com.example.planms.model.enums.GoalType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PlanItemRequestDto {
    int index;
    String text;
    String status;
    LocalDate planDate;

    int containerIndex;
    GoalType goalType;
}


