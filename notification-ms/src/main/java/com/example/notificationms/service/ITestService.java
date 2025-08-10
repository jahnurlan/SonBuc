package com.example.notificationms.service;

import org.springframework.http.ResponseEntity;

public interface ITestService {
    ResponseEntity<String> sendTestEmail();
}
