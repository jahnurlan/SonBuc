package com.example.chatms.service.impl;

import com.example.chatms.model.dto.request.ChatMessageRequestDto;
import com.example.chatms.model.dto.response.MessageResponseDto;
import com.example.chatms.model.entity.ChatMessage;
import com.example.chatms.model.enums.MessageType;
import com.example.chatms.repository.ChatMessageRepository;
import com.example.chatms.service.IMessageService;
import com.example.chatms.util.Helper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class MessageService implements IMessageService {
    private final ChatMessageRepository messageRepository;
    private final Helper helper;

    @Transactional
    @Override
    public List<MessageResponseDto> allMessages(String token, String recipient) {
        String username = helper.getUsername(token);
        log.info("JWT'den çıkarılan kullanıcı adı: {}", username);
        log.info("Alıcı: {}", recipient);

        // Veritabanında SQL ile güncelleme
        messageRepository.updateSeenStatusBySenderAndRecipient(username, recipient);

        return messageRepository.findChatBetweenUsers(username, recipient)
                .orElse(List.of()) // Eğer sonuç boşsa, boş bir liste döner.
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private MessageResponseDto convertToDto(ChatMessage message) {
        if (message.getType() == MessageType.PLAN_REPLY || message.getType() == MessageType.CHAT_REPLY) {
            return MessageResponseDto.builder()
                    .id(message.getId())
                    .content(message.getContent())
                    .type(message.getType())
                    .sender(message.getSender())
                    .recipient(message.getRecipient())
                    .timestamp(message.getTimestamp())
                    .seenStatus(message.isSeenStatus())
                    .replyMessage(message.getReplyMessage())
                    .status(message.isStatus())
                    .build();
        } else if (message.getType() == MessageType.DELETE) {
            return MessageResponseDto.builder()
                    .id(message.getId())
                    .sender(message.getSender())
                    .recipient(message.getRecipient())
                    .timestamp(message.getTimestamp())
                    .seenStatus(message.isSeenStatus())
                    .type(message.getType())
                    .build();
        } else if (message.getType() == MessageType.CHAT) {
            return MessageResponseDto.builder()
                    .id(message.getId())
                    .content(message.getContent())
                    .type(message.getType())
                    .sender(message.getSender())
                    .recipient(message.getRecipient())
                    .timestamp(message.getTimestamp())
                    .seenStatus(message.isSeenStatus())
                    .status(message.isStatus())
                    .build();
        } else {//CHAT | EDIT
            return MessageResponseDto.builder()
                    .id(message.getId())
                    .content(message.getContent())
                    .type(message.getType())
                    .sender(message.getSender())
                    .recipient(message.getRecipient())
                    .timestamp(message.getTimestamp())
                    .seenStatus(message.isSeenStatus())
                    .planContainerList(message.getPlanContainerList())
                    .replyMessage(message.getReplyMessage())
                    .status(message.isStatus())
                    .build();
        }
    }

    @Override
    public boolean setMessageStatusToFalse(ChatMessageRequestDto requestDto, String username) {
        Optional<ChatMessage> optionalChatMessage = messageRepository.findById(requestDto.getMessageId());

        if (optionalChatMessage.isPresent()) {
            ChatMessage chatMessage = optionalChatMessage.get();

            if (chatMessage.getSender().equals(username)) {
                chatMessage.setStatus(false);
                chatMessage.setType(MessageType.DELETE);
                messageRepository.save(chatMessage);
                log.info("Chat message deleted successfully...");
                return true;
            } else {
                log.warn("ChatMessage username and token username not equals... ChatMessage=> {} || username=> {}", chatMessage, username);
                return false;
            }
        } else {
            log.warn("ChatMessage not found..!");
            return false;
        }
    }
}
