package com.example.chatms.service;

import com.example.chatms.model.dto.request.ChatMessageRequestDto;
import com.example.chatms.model.dto.response.MessageResponseDto;

import java.util.List;

public interface IMessageService {
    List<MessageResponseDto> allMessages(String token,String recipient);

    boolean setMessageStatusToFalse(ChatMessageRequestDto requestDto, String username);
}
