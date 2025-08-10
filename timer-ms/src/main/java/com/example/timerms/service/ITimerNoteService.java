package com.example.timerms.service;

import com.example.timerms.model.dto.request.*;
import com.example.timerms.model.entity.TimeNote;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;

public interface ITimerNoteService {
    ResponseEntity<Long> addTimerNote(AddTimeNoteRequestDto requestDto, String jwtToken);

    ResponseEntity<String> deleteTimerNote(Long id, String jwtToken);

    ResponseEntity<Long> connectTimerNoteToPlan(TimerConnectRequestDto timerConnectRequestDto, String jwtToken);

    ResponseEntity<Long> updateTimerNoteTextRequest(TimerTextRequestDto timerTextRequestDto, String jwtToken);
    void updateTimerNoteText(KafkaRequest kafkaRequest);

    ResponseEntity<List<TimeNote>> getAllTimeNotes(LocalDate day, String jwtToken);

    ResponseEntity<Long> startTimer(TimerRequestDto timerRequestDto, String jwtToken);

    ResponseEntity<String> stopTimer(TimerRequestDto timerRequestDto, String jwtToken);

    ResponseEntity<String> resetTimer(TimerRequestDto timerRequestDto, String jwtToken);
}
