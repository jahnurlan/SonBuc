package com.example.planms.model.dto.request;

import com.example.planms.model.enums.GoalType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GetGoalRequestDto {
    @NotNull(message = "Plan date required")
    LocalDate planDate;
    GoalType type;
}
