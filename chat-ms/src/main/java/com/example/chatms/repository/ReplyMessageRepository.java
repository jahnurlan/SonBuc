package com.example.chatms.repository;

import com.example.chatms.model.entity.ReplyMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReplyMessageRepository extends JpaRepository<ReplyMessage,Long> {

}
