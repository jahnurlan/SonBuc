package com.example.timerms.util;

import com.example.commonsecurity.auth.services.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.time.LocalTime;
import java.time.OffsetDateTime;

@RequiredArgsConstructor
@Slf4j
@Configuration
public class Helper {
    private final JwtService jwtService;

    public String getUsername(String jwtToken) {
        String jwt = jwtToken.substring(7);
        return jwtService.extractUsername(jwt);
    }

    // Helper method to format OffsetDateTime to a more readable string
    public String formatDateTime(OffsetDateTime dateTime) {
        return dateTime != null ? dateTime.toLocalDateTime().toString() : "N/A";
    }

    // Helper method to format LocalTime (duration) to a more readable string
    public String formatDuration(LocalTime duration) {
        return duration != null ? duration.toString() : "00:00:00";
    }

    // New duration calculation logic
    public long calculateNewDuration(OffsetDateTime startTime, OffsetDateTime stopTime, LocalTime previousDuration) {
        long seconds = Duration.between(startTime, stopTime != null ? stopTime : OffsetDateTime.now()).toSeconds();
        long previousSeconds = previousDuration != null ? previousDuration.toSecondOfDay() : 0;
        return previousSeconds + seconds;
    }
}
