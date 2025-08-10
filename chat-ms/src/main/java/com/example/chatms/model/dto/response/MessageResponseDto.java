package com.example.chatms.model.dto.response;

import com.example.chatms.model.entity.PlanContainer;
import com.example.chatms.model.entity.PlanItem;
import com.example.chatms.model.entity.ReplyMessage;
import com.example.chatms.model.enums.MessageType;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.time.ZonedDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL) // Excludes null values
public class MessageResponseDto {
    Long id;
    String sender;
    String recipient;
    String content;
    ZonedDateTime timestamp;
    boolean seenStatus;

    @Enumerated(EnumType.STRING)
    MessageType type;

    List<PlanContainer> planContainerList;

    ReplyMessage replyMessage;
    boolean status;
}
