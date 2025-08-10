package com.example.chatms.service.impl;

import com.example.chatms.model.entity.UserLastSeen;
import com.example.chatms.repository.UserLastSeenRepository;
import com.example.chatms.service.IUserLastSeenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserLastSeenSeenService implements IUserLastSeenService {
    private final UserLastSeenRepository userLastSeenRepository;

    @Override
    public ResponseEntity<ZonedDateTime> getUserLastSeenTime(String username) {
        Optional<UserLastSeen> byUsername = userLastSeenRepository.findByUsername(username);

        return byUsername.map(userLastSeen -> ResponseEntity.ok().body(userLastSeen.getLastSeen()))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }
}
