package com.example.planms.model.dto.request;

import com.example.planms.model.enums.PlanType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EditTextRequestDto {
    String planType;
    int planIndex;
    String newPlanText;
    LocalDateTime planDate;
}
















