package com.example.chatms.repository;

import com.example.chatms.model.entity.UserLastSeen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserLastSeenRepository extends JpaRepository<UserLastSeen,Long> {
    Optional<UserLastSeen> findByUsername(String username);
}
