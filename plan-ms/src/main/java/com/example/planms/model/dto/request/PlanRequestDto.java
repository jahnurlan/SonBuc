package com.example.planms.model.dto.request;

import com.example.planms.model.enums.PlanType;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PlanRequestDto {
    @NotNull(message = "Plan date required")
    LocalDate planDate;
    List<PlanContainerRequestDto> planContainerRequestDtoList;

    @Builder.Default
    PlanType planType = PlanType.NORMAL;
}
