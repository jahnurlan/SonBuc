package com.example.chatms.model.entity;

import com.example.chatms.model.enums.MessageType;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String sender;
    String recipient;

    @Column(length = 2000)
    String content;
    boolean seenStatus;

    @Builder.Default
    ZonedDateTime timestamp = ZonedDateTime.now().withZoneSameInstant(ZoneId.of("UTC"));

    @Enumerated(EnumType.STRING)
    MessageType type;

    @OneToOne
    ReplyMessage replyMessage;

    @OneToMany(mappedBy = "chatMessage", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    List<PlanContainer> planContainerList;

    @Builder.Default
    boolean status = true;
}
