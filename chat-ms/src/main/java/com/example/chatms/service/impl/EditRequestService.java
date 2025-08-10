package com.example.chatms.service.impl;


import com.example.chatms.model.dto.request.EditStatusRequestDto;
import com.example.chatms.model.dto.request.KafkaRequest;
import com.example.chatms.model.entity.ChatMessage;
import com.example.chatms.model.entity.PlanContainer;
import com.example.chatms.model.entity.PlanItem;
import com.example.chatms.repository.ChatMessageRepository;
import com.example.chatms.repository.PlanContainerRepository;
import com.example.chatms.repository.PlanItemRepository;
import com.example.chatms.service.IEditRequestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class EditRequestService implements IEditRequestService {
    private final ChatMessageRepository chatMessageRepository;
    private final PlanItemRepository planItemRepository;
    private final PlanContainerRepository planContainerRepository;
    private final KafkaTemplate<String, KafkaRequest> kafkaTemplate;

    private static final String STATUS_ACCEPT = "accept";
    private static final String STATUS_REJECT = "reject";

    @Override
    @Transactional
    public ChatMessage saveEditRequest(ChatMessage chatMessage, List<PlanContainer> planContainerListRequest) {
        log.info("Saving edit request for ChatMessage ID: {}", chatMessage.getId());
        if (planContainerListRequest == null || planContainerListRequest.isEmpty()) {
            log.warn("Plan container list is empty, skipping update.");
            return chatMessage;
        }

        chatMessage.setTimestamp(ZonedDateTime.now());
        chatMessage.setPlanContainerList(new ArrayList<>());

        for (PlanContainer planContainerRequest : planContainerListRequest){
            //Plan Container created
            PlanContainer planContainer = planContainerRepository.save(
                    PlanContainer.builder()
                            .name(planContainerRequest.getName())
                            .index(planContainerRequest.getIndex())
                            .chatMessage(chatMessage)
                    .build()
            );

            List<PlanItem> savedPlanItems = planContainerRequest.getPlanItemList().stream()
                    .map(planItemDto -> PlanItem.builder()
                            .index(planItemDto.getIndex())
                            .type(planItemDto.getType())
                            .text(planItemDto.getText())
                            .status(planItemDto.getStatus())
                            .username(chatMessage.getRecipient())
                            .planContainer(planContainer)
                            .build())
                    .map(planItemRepository::save)
                    .collect(Collectors.toCollection(ArrayList::new));

            planContainer.setPlanItemList(savedPlanItems);
            chatMessage.getPlanContainerList().add(planContainer);
        }

        log.info("ChatMessage updated with PlanItems: {}", chatMessage.getPlanContainerList().size());
        return chatMessage;
    }

    @Override
    @Transactional
    public ResponseEntity<String> rejectEditRequest(EditStatusRequestDto requestDto) {
        Optional<ChatMessage> optionalChatMessage = chatMessageRepository.findById(requestDto.getId());
        if (optionalChatMessage.isPresent()){
            ChatMessage chatMessage = optionalChatMessage.get();
            setPlanItemStatus(requestDto, STATUS_REJECT, chatMessage);

            return ResponseEntity.ok().body(null);
        }else{
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @Override
    @Transactional
    public ResponseEntity<String> acceptEditRequest(EditStatusRequestDto requestDto) {
        Optional<ChatMessage> optionalChatMessage = chatMessageRepository.findById(requestDto.getId());
        if (optionalChatMessage.isPresent()){
            ChatMessage chatMessage = optionalChatMessage.get();
            log.info("ChatMessage found in acceptEditRequest => {}",chatMessage);

            setPlanItemStatus(requestDto, STATUS_ACCEPT, chatMessage);
            sendKafkaRequestForUpdatingPlanText(requestDto, chatMessage.getTimestamp().toLocalDate());

            return ResponseEntity.ok().body(null);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Message not found!");
        }

    }

    private List<PlanContainer> setPlanItemStatus(EditStatusRequestDto requestDto,String status, ChatMessage message){
        log.info("WebSocket Accept condition is working...");
        log.info("Request Dto => {} -- ChatMessage => {}", requestDto, message);

        // Güncellenmiş PlanContainer listesi
        return requestDto.getPlanContainerList().stream()
                .map(requestContainer -> {
                    // ChatMessage içindeki eşleşen PlanContainer'ı bul
                    Optional<PlanContainer> matchedContainerOpt = message.getPlanContainerList().stream()
                            .filter(existingContainer -> existingContainer.getIndex() == requestContainer.getIndex())
                            .findFirst();

                    if (matchedContainerOpt.isPresent()) {
                        PlanContainer matchedContainer = matchedContainerOpt.get();
                        log.info("Matched container found => {}", matchedContainer);

                        matchedContainer.getPlanItemList().stream()
                                .filter(planItem -> matchesRequestItem(planItem, requestContainer.getPlanItemList(), STATUS_ACCEPT))
                                .forEach(planItem -> {
                                    planItem.setStatus(status);
                                    log.info("{} index container planItem updated => {}",matchedContainer.getIndex(),planItem);
                                    planItemRepository.save(planItem);
                                });
                        return matchedContainer;
                    } else {
                        log.info("Matched container not found. RequestDto => {}", requestDto);

                        // Eğer eşleşen konteyner yoksa, olduğu gibi ekle (tüm planItem'ları ile)
                        return requestContainer;
                    }
                })
                .collect(Collectors.toList());
    }

    private boolean matchesRequestItem(PlanItem planItem, List<PlanItem> requestItems, String status) {
        return requestItems.stream()
                .anyMatch(requestItem -> planItem.getIndex() == requestItem.getIndex()
                        && planItem.getType().equals(requestItem.getType()));
    }

    private void sendKafkaRequestForUpdatingPlanText(EditStatusRequestDto requestDto, LocalDate planDate) {
        KafkaRequest kafkaRequest = KafkaRequest.builder()
                .firstUser(requestDto.getFirstUser())
                .secondUser(requestDto.getSecondUser())
                .planDate(planDate)
                .planContainerList(requestDto.getPlanContainerList())
                .build();
        kafkaTemplate.send("accept-edit-message-topic", kafkaRequest);
        log.info("Kafka request sent: {}", kafkaRequest);
    }
}

