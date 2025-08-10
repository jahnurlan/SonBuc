package com.example.timerms.repository;

import com.example.timerms.model.entity.TimeNote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TimeNoteRepository extends JpaRepository<TimeNote, Long> {
    List<TimeNote> findAllByUsernameAndDay(String username, LocalDate day);

    @Query("SELECT COUNT(tn) FROM TimeNote tn")
    long countAllTimeNotes();

    @Query("SELECT COUNT(tn) FROM TimeNote tn WHERE tn.day = :today")
    long countTimeNotesByDay(LocalDate today);
}
