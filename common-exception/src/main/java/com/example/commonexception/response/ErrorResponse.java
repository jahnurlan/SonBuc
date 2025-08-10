package com.example.commonexception.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {

    private String message;
    private int httpStatus;
    private LocalDateTime responseTime;

    public static ErrorResponse of(String message, int httpStatus ){
        return build(message,httpStatus);
    }

    public static ErrorResponse build(String message, int httpStatus){

        return ErrorResponse.builder()
                .message(message)
                .httpStatus(httpStatus)
                .responseTime(LocalDateTime.now().withNano(0))
                .build();
    }

}
