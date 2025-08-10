package com.example.chatms.websocket;

import com.example.chatms.model.entity.ChatMessage;
import com.example.chatms.model.entity.UserLastSeen;
import com.example.chatms.model.enums.MessageType;
import com.example.chatms.repository.UserLastSeenRepository;
import com.example.chatms.service.IUserStatusService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.time.ZonedDateTime;
import java.util.Objects;

@Component
@Slf4j
@RequiredArgsConstructor
public class WebSocketEventListener {
    private final SimpMessageSendingOperations messageTemplate;
    private final UserLastSeenRepository userLastSeenRepository;
    private final IUserStatusService userStatusService;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        // Header'lardan sender ve recipient bilgilerini alın
        String sender = headerAccessor.getFirstNativeHeader("sender");
        String recipient = headerAccessor.getFirstNativeHeader("recipient");

        if (sender != null && recipient != null) {
            // Session attributes içine sender ve recipient bilgilerini kaydedin
            Objects.requireNonNull(headerAccessor.getSessionAttributes()).put("sender", sender);
            headerAccessor.getSessionAttributes().put("recipient", recipient);

            // Kullanıcının katıldığına dair bir "JOIN" mesajı gönderin
            ChatMessage joinMessage = ChatMessage.builder()
                    .sender("System")
                    .recipient(recipient)
                    .content(sender + " has joined the chat.")
                    .type(MessageType.JOIN)
                    .timestamp(ZonedDateTime.now())
                    .build();

            messageTemplate.convertAndSendToUser(recipient, "/queue/messages", joinMessage);
            log.info("User connected: {}", sender);
        }

        //TODO save to redis user On-Chat status
        userStatusService.setUserOnChat(sender);
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sender = (String) Objects.requireNonNull(headerAccessor.getSessionAttributes()).get("sender");
        String recipient = (String) headerAccessor.getSessionAttributes().get("recipient");

        if (sender != null && recipient != null) {
            UserLastSeen userLastSeen = new UserLastSeen();
            userLastSeen.setUsername(sender);
            userLastSeen.setLastSeen(ZonedDateTime.now());
            userLastSeenRepository.save(userLastSeen);

            ChatMessage leaveMessage = ChatMessage.builder()
                    .sender("System")
                    .recipient(recipient)
                    .content(sender + " has left the chat.")
                    .type(MessageType.LEAVE)
                    .timestamp(ZonedDateTime.now())
                    .build();

            messageTemplate.convertAndSendToUser(recipient, "/queue/messages", leaveMessage);
            log.info("User disconnected: {}", sender);

            //TODO set to user offline status
            userStatusService.setUserOffline(sender);
        }
    }
}
