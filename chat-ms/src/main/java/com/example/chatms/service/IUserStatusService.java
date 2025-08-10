package com.example.chatms.service;

import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface IUserStatusService {
    void setUserOnline(String username);
    void setUserOnChat(String username);
    void setUserOffline(String username);
    ResponseEntity<Map<String,String>> isUserOnline(String username);
}
