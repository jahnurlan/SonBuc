package com.example.chatms.service;
import org.springframework.http.ResponseEntity;
import java.time.ZonedDateTime;

public interface IUserLastSeenService {
    ResponseEntity<ZonedDateTime> getUserLastSeenTime(String username);
}
