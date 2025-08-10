package com.example.chatms.controller;

import com.example.chatms.model.dto.response.MessageResponseDto;
import com.example.chatms.service.IMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/msg")
public class MessageController {
    private final IMessageService messageService;

    @GetMapping("/messages")
    public List<MessageResponseDto> getMessages(@RequestHeader(name = HttpHeaders.AUTHORIZATION) String token, @RequestParam String recipient) {
        log.info("getMessages method working");
        return messageService.allMessages(token,recipient);
    }
}