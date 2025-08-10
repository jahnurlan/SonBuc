package com.example.chatms.repository;

import com.example.chatms.model.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage,Long> {
    @Query("SELECT m FROM ChatMessage m WHERE (m.sender = :sender AND m.recipient = :recipient) OR (m.sender = :recipient AND m.recipient = :sender)")
    Optional<List<ChatMessage>> findChatBetweenUsers(@Param("sender") String sender, @Param("recipient") String recipient);

    @Modifying
    @Query("UPDATE ChatMessage m SET m.seenStatus = TRUE WHERE m.sender = :recipient AND m.recipient = :sender AND m.seenStatus = FALSE")
    void updateSeenStatusBySenderAndRecipient(@Param("sender") String sender, @Param("recipient") String recipient);

    @Query("SELECT m FROM ChatMessage m " +
            "WHERE ((m.sender = :sender AND m.recipient = :recipient) " +
            "OR (m.sender = :recipient AND m.recipient = :sender)) " +
            "AND m.type = 'EDIT' " +
            "AND DATE(m.timestamp) = :planDate")
    Optional<ChatMessage> findEditMessagesBySenderRecipientAndDate(
            @Param("sender") String sender,
            @Param("recipient") String recipient,
            @Param("planDate") LocalDate planDate);

}
