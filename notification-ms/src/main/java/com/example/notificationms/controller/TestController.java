package com.example.notificationms.controller;

import com.example.notificationms.service.ITestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class TestController {
    private final ITestService testService;

    @GetMapping("/test")
    public ResponseEntity<String> sendTestMail(){
        log.info("Test mail controller working!");
        return testService.sendTestEmail();
    }
}
