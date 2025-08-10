package com.example.notificationms.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Slf4j
@Service
public class TestService implements ITestService{
    private final JavaMailSender javaMailSender;

    public ResponseEntity<String> sendTestEmail() {
        log.info("Test mail service is working...");

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo("cahangirzadenurlan043@gmail.com"); // alıcı adresi
        message.setSubject("Test Mail");
        message.setText("Merhaba, bu bir test e-postasıdır.");
        message.setFrom("info@sonbuc.com");

        javaMailSender.send(message);
        return ResponseEntity.ok("Mail sent!");
    }
}
