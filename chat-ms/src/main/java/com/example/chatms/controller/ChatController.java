package com.example.chatms.controller;

import com.example.chatms.model.dto.request.ChatMessageRequestDto;
import com.example.chatms.model.dto.response.DeleteMessageResponseDto;
import com.example.chatms.model.dto.response.MessageIdResponseDto;
import com.example.chatms.model.entity.ChatMessage;
import com.example.chatms.model.entity.PlanContainer;
import com.example.chatms.model.entity.PlanItem;
import com.example.chatms.model.entity.ReplyMessage;
import com.example.chatms.model.enums.MessageType;
import com.example.chatms.repository.ChatMessageRepository;
import com.example.chatms.repository.ReplyMessageRepository;
import com.example.chatms.service.IEditRequestService;
import com.example.chatms.service.IMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequiredArgsConstructor
public class ChatController {
    private final SimpMessagingTemplate messagingTemplate;
    private final ChatMessageRepository chatMessageRepository;
    private final ReplyMessageRepository replyMessageRepository;
    private final IEditRequestService editRequestService;
    private final IMessageService messageService;

    @MessageMapping("/chat.sendMessage")
    @Transactional
    public void sendMessage(@Payload ChatMessageRequestDto requestDto, StompHeaderAccessor accessor) {
        log.info("sendMessage method is working...");
        log.info("chatMessage => {}",requestDto.toString());

        // SessionAttributes’dan username bilgisini alın
        String usernameFromToken = (String) Objects.requireNonNull(accessor.getSessionAttributes()).get("username");
        log.info("usernameFrom token => {}",usernameFromToken);
        log.info("recipient username => {}",requestDto.getRecipient());

        ChatMessage chatMessage = ChatMessage.builder()
                .sender(usernameFromToken)
                .recipient(requestDto.getRecipient())
                .seenStatus(requestDto.isSeenStatus())
                .content(requestDto.getContent())
                .type(requestDto.getType())
                .build();

        log.info("ChatMessage created => {}",chatMessage);

        switch (requestDto.getType()) {
            case CHAT_REPLY:
                saveReplyMessage(requestDto, chatMessage);
                break;
            case PLAN_REPLY:
                savePlanReplyMessage(requestDto, chatMessage);
                break;
            case EDIT:
                chatMessage = editRequestService.saveEditRequest(chatMessage, requestDto.getPlanContainerList());
                break;
            case ACCEPT, REJECT:
                setPlanItemList(requestDto, chatMessage);
                return;
            case DELETE:
                deleteMessage(requestDto, usernameFromToken);
                return;
            default:
                log.warn("Unknown message type: {}", requestDto.getType());
        }

        log.info("Before saving...");
        ChatMessage savedMessage = chatMessageRepository.save(chatMessage);
        log.info("After saving...");

        // Mesajı hedef kullanıcıya gönderin
        sendMessageToUser(chatMessage.getRecipient(),savedMessage);

        //Sendere guncel mesajin id degerini gonderirem
        sendMessageIdToSender(requestDto.getMessageId(),savedMessage.getId(),savedMessage.getSender());
    }

    private void sendMessageIdToSender(Long passedId,Long messageId,String recipient){
        MessageIdResponseDto messageIdResponseDto = MessageIdResponseDto.builder()
                .passedId(passedId)
                .messageId(messageId)
                .type("ID")
                .build();

        messagingTemplate.convertAndSendToUser(recipient, "/queue/messages", messageIdResponseDto);
    }

    @Transactional
    public void deleteMessage(ChatMessageRequestDto requestDto, String sender) {
        if(messageService.setMessageStatusToFalse(requestDto,sender)){
            log.info("setMessageStatusToFalse boolean working...");
            DeleteMessageResponseDto deleteMessageResponseDto = DeleteMessageResponseDto.builder()
                    .messageId(requestDto.getMessageId())
                    .type(MessageType.DELETE)
                    .sender(sender)
                    .recipient(requestDto.getRecipient())
                    .build();

            // Mesajı hedef kullanıcıya gönderin
            messagingTemplate.convertAndSendToUser(requestDto.getRecipient(), "/queue/messages", deleteMessageResponseDto);
        }
    }

    private void setPlanItemList(ChatMessageRequestDto requestDto, ChatMessage chatMessage) {
        log.error("----------------------------");
        log.info("WebSocket Accept condition is working...");
        log.error("----------------------------");

        Optional<ChatMessage> optionalChatMessage = chatMessageRepository.findById(requestDto.getMessageId());
        if (optionalChatMessage.isPresent()) {
            ChatMessage message = optionalChatMessage.get();

            chatMessage.setId(message.getId());
            chatMessage.setPlanContainerList(requestDto.getPlanContainerList());
            log.info("Accept ChatMessage created and set=> {}", chatMessage);

            sendMessageToUser(chatMessage.getRecipient(), chatMessage);
        }
    }

    private void sendMessageToUser(String recipient, ChatMessage chatMessage) {
        messagingTemplate.convertAndSendToUser(recipient, "/queue/messages", chatMessage);
    }

    private void saveReplyMessage(ChatMessageRequestDto requestDto, ChatMessage chatMessage) {
        saveReplyMessage(requestDto, chatMessage, builder -> {}, MessageType.CHAT_REPLY);
    }

    private void savePlanReplyMessage(ChatMessageRequestDto requestDto, ChatMessage chatMessage) {
        saveReplyMessage(requestDto, chatMessage, builder -> {
            builder.planId(requestDto.getPlanId())
                    .planType(requestDto.getPlanType());
        }, MessageType.PLAN_REPLY);
    }

    private void saveReplyMessage(ChatMessageRequestDto requestDto, ChatMessage chatMessage,
                                  Consumer<ReplyMessage.ReplyMessageBuilder> additionalSetup, MessageType messageType) {
        ReplyMessage.ReplyMessageBuilder builder = ReplyMessage.builder()
                .username(requestDto.getUsername())
                .content(requestDto.getReplyMessageContent())
                .messageId(requestDto.getReplyId());

        // Ek alanların yapılandırılması
        additionalSetup.accept(builder);

        ReplyMessage replyMessage = builder.build();
        replyMessageRepository.save(replyMessage);

        chatMessage.setReplyMessage(replyMessage);
        chatMessage.setType(messageType);

        log.info("Reply Message created => {}", replyMessage);
    }

}








