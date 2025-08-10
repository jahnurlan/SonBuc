package com.example.chatms.model.dto.response;

import com.example.chatms.model.enums.MessageType;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DeleteMessageResponseDto {
    Long messageId;
    String sender;
    String recipient;
    MessageType type;
}
