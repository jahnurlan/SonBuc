package com.example.planms.model.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PlanConnectRequestDto {
    @NotNull(message = "Plan date required")
    LocalDate planDate;
    Long goalId;
}

