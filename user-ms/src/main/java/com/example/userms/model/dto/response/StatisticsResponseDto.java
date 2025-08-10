package com.example.userms.model.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StatisticsResponseDto {
    long allUserCount;
    long allGuestUserCount;

    long allTodayRegisteredUserCount;
    long allTodayRegisteredGuestUserCount;

    long inActiveUserCount;
}
