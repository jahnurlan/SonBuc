package com.example.planms.model.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SharedPlanHistoryRequestDto {
    String firstUsername;
    String secondUsername;
}

