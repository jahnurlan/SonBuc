package com.example.chatms.model.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;
import javax.persistence.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReplyMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    Long messageId;
    String username;
    String content;

    int planId;
    String planType;
}
