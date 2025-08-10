package com.example.planms.model.dto.response.connect_goal;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ConnectPlanResponseDto {
    Long id;
    LocalDate date;
    String note;
}






