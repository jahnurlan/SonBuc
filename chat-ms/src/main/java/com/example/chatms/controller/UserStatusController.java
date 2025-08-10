package com.example.chatms.controller;

import com.example.chatms.service.IUserLastSeenService;
import com.example.chatms.service.IUserStatusService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/us")
public class UserStatusController {
    private final IUserLastSeenService userLastSeenService;
    private final IUserStatusService userStatusService;

    // Kullanıcının online durumunu güncelleme
    @PostMapping("/online")
    public void setUserOnline(@RequestParam String username) {
        userStatusService.setUserOnline(username);
    }

    // Kullanıcının offline durumunu güncelleme
    @PostMapping("/offline")
    public void setUserOffline(@RequestParam String username) {
        userStatusService.setUserOffline(username);
    }

    // Belirli bir kullanıcının online olup olmadığını sorgulama
    @GetMapping("/is-online")
    public ResponseEntity<Map<String,String>> isUserOnline(@RequestParam String friendUsername) {
        return userStatusService.isUserOnline(friendUsername);
    }
}
