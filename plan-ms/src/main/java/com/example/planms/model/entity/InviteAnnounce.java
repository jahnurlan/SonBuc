package com.example.planms.model.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InviteAnnounce {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    LocalDateTime announceDate;
    LocalDateTime planDate;

    @Column(name = "invited_username")
    String invited_username;
    String username;

    @Builder.Default
    boolean status = true;

    boolean readingStatus;
}

