package com.example.chatms.service.impl;

import com.example.chatms.model.entity.UserLastSeen;
import com.example.chatms.repository.UserLastSeenRepository;
import com.example.chatms.service.IUserStatusService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserStatusService implements IUserStatusService {

    private final UserLastSeenRepository userLastSeenRepository;
    private static final String ONLINE_STATUS_PREFIX = "online:";
    private static final String ON_CHAT_STATUS_PREFIX = "on_chat:";
    private static final String LAST_SEEN_PREFIX = "lastSeen:";
    private final StringRedisTemplate stringRedisTemplate;

    /**
     * Set user status to online, removing on_chat status if it exists.
     * If the user is currently on_chat, the online status update is skipped.
     */
    @Transactional
    @Async
    public void setUserOnline(String username) {
        log.info("Setting user {} status to online", username);

        String onlineKey = ONLINE_STATUS_PREFIX + username;
        String onChatKey = ON_CHAT_STATUS_PREFIX + username;
        String lastSeenKey = LAST_SEEN_PREFIX + username;

        // Check if user is currently in on_chat status; if so, skip setting online status
        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(onChatKey))) {
            log.info("User {} is currently in on_chat status, skipping online status update.", username);
            return;
        }

        // Remove on_chat status if any
        stringRedisTemplate.delete(onChatKey);

        // Set user to online with TTL
        stringRedisTemplate.opsForValue().set(onlineKey, "online", 50, TimeUnit.SECONDS);

        // Save last seen with 6-hour TTL
        stringRedisTemplate.opsForValue().set(lastSeenKey, ZonedDateTime.now().toString(), 6, TimeUnit.HOURS);
    }

    /**
     * Set user status to on_chat, removing online status if it exists.
     */
    @Transactional
    @Async
    public void setUserOnChat(String username) {
        log.info("Setting user {} status to on_chat", username);

        String onlineKey = ONLINE_STATUS_PREFIX + username;
        String onChatKey = ON_CHAT_STATUS_PREFIX + username;
        String lastSeenKey = LAST_SEEN_PREFIX + username;

        // Remove online status
        stringRedisTemplate.delete(onlineKey);

        // Set user to on_chat without TTL
        stringRedisTemplate.opsForValue().set(onChatKey, "on_chat");

        // Save last seen with 6-hour TTL
        stringRedisTemplate.opsForValue().set(lastSeenKey, ZonedDateTime.now().toString(), 6, TimeUnit.HOURS);
    }

    /**
     * Set user status to offline. If the user is on_chat, remove on_chat status
     * and set the user to online. Otherwise, remove the online status.
     */
    @Transactional
    @Async
    public void setUserOffline(String username) {
        log.info("Setting user {} status to offline", username);

        String onlineKey = ONLINE_STATUS_PREFIX + username;
        String onChatKey = ON_CHAT_STATUS_PREFIX + username;
        String lastSeenKey = LAST_SEEN_PREFIX + username;

        // Check if user is in on_chat status
        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(onChatKey))) {
            // Remove on_chat status and set user to online
            stringRedisTemplate.delete(onChatKey);
            stringRedisTemplate.opsForValue().set(onlineKey, "online", 50, TimeUnit.SECONDS);
            log.info("User {} was on_chat, set to online instead.", username);
        } else {
            // If user is not on_chat, remove online status
            stringRedisTemplate.delete(onlineKey);
            log.info("User {} is set to offline.", username);
        }

        // Update lastSeen in Redis
        stringRedisTemplate.opsForValue().set(lastSeenKey, ZonedDateTime.now().toString());
    }

    /**
     * Check if the user is online or on_chat.
     * If offline, return the last seen information from Redis or database.
     */
    @Override
    public ResponseEntity<Map<String, String>> isUserOnline(String username) {
        log.info("Checking online status for user {}", username);

        String onlineKey = ONLINE_STATUS_PREFIX + username;
        String onChatKey = ON_CHAT_STATUS_PREFIX + username;
        String lastSeenKey = LAST_SEEN_PREFIX + username;
        Map<String, String> response = new HashMap<>();

        // Check if user is on_chat
        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(onChatKey))) {
            log.info("User {} is ON_CHAT", username);
            response.put("type", "on_chat");
            return ResponseEntity.ok().body(response);
        }

        // Check if user is online
        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(onlineKey))) {
            log.info("User {} is ONLINE", username);
            response.put("type", "online");
            return ResponseEntity.ok().body(response);
        }

        // If offline, get last seen from Redis
        String lastSeen = stringRedisTemplate.opsForValue().get(lastSeenKey);
        if (lastSeen != null) {
            log.info("User {} is offline, last seen in Redis: {}", username, lastSeen);
            response.put("lastSeen", lastSeen);
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
        }

        // If last seen not found in Redis, fetch from database
        log.info("User {} not found in Redis, fetching from database", username);
        Optional<UserLastSeen> userLastSeen = userLastSeenRepository.findByUsername(username);

        return userLastSeen
                .map(lastSeenData -> {
                    // Add lastSeen to Redis with 6-hour TTL
                    String lastSeenValue = lastSeenData.getLastSeen().toString();
                    stringRedisTemplate.opsForValue().set(lastSeenKey, lastSeenValue, 6, TimeUnit.HOURS);
                    log.info("User {} last seen added to Redis with TTL: {}", username, lastSeenValue);

                    response.put("lastSeen", lastSeenValue);
                    return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "user not found in lastSeen db")));
    }


    /**
     * Scheduled task to save all lastSeen data from Redis to database every 5 hours.
     */
    @Scheduled(fixedRate = 5 * 60 * 60 * 1000)
    @Transactional
    @Async
    public void saveAllLastSeenToDatabase() {
        log.info("Scheduled task: Saving all lastSeen data from Redis to database.");

        // Retrieve all lastSeen keys from Redis
        Set<String> keys = stringRedisTemplate.keys(LAST_SEEN_PREFIX + "*");
        if (keys != null && !keys.isEmpty()) {
            for (String key : keys) {
                String username = key.replace(LAST_SEEN_PREFIX, "");
                String lastSeenValue = stringRedisTemplate.opsForValue().get(key);

                if (lastSeenValue != null) {
                    ZonedDateTime lastSeen = ZonedDateTime.parse(lastSeenValue);

                    // Save to database
                    Optional<UserLastSeen> byUsername = userLastSeenRepository.findByUsername(username);
                    UserLastSeen userLastSeen = byUsername.orElseGet(() -> UserLastSeen.builder()
                            .username(username)
                            .build());
                    userLastSeen.setLastSeen(lastSeen);

                    userLastSeenRepository.save(userLastSeen);
                    log.info("Saved lastSeen for user {} to database.", username);
                }
            }
        }
    }
}
