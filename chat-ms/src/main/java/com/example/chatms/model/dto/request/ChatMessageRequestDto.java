package com.example.chatms.model.dto.request;

import com.example.chatms.model.entity.PlanContainer;
import com.example.chatms.model.entity.PlanItem;
import com.example.chatms.model.enums.MessageType;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChatMessageRequestDto {
    long messageId;
    String sender;
    String recipient;

    String username;
    String content;

    long replyId;
    String replyMessageContent;
    int planId;
    String planType;

    boolean seenStatus;

    @Enumerated(EnumType.STRING)
    MessageType type;

    List<PlanContainer> planContainerList;
}