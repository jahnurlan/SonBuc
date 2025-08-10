package com.example.timerms.controller;

import com.example.timerms.model.dto.request.AddTimeNoteRequestDto;
import com.example.timerms.model.dto.request.TimerConnectRequestDto;
import com.example.timerms.model.dto.request.TimerRequestDto;
import com.example.timerms.model.dto.request.TimerTextRequestDto;
import com.example.timerms.model.entity.TimeNote;
import com.example.timerms.service.ITimerNoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/time-note")
public class TimeNoteController {
    private final ITimerNoteService timeNoteService;

    @GetMapping("/{day}")
    public ResponseEntity<List<TimeNote>> getAllTimeNotesByDay(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate day,
            @RequestHeader(name = HttpHeaders.AUTHORIZATION) String jwtToken) {
        return timeNoteService.getAllTimeNotes(day, jwtToken);
    }

    @PostMapping("/start")
    public ResponseEntity<Long> startTimeNote(@RequestBody TimerRequestDto timerRequestDto, @RequestHeader(name = HttpHeaders.AUTHORIZATION) String jwtToken){
        return timeNoteService.startTimer(timerRequestDto, jwtToken);
    }

    @PutMapping("/stop")
    public ResponseEntity<String> stopTimeNote(@RequestBody TimerRequestDto timerRequestDto, @RequestHeader(name = HttpHeaders.AUTHORIZATION) String jwtToken){
        return timeNoteService.stopTimer(timerRequestDto, jwtToken);
    }

    @PutMapping("/reset")
    public ResponseEntity<String> resetTimeNote(@RequestBody TimerRequestDto timerRequestDto, @RequestHeader(name = HttpHeaders.AUTHORIZATION) String jwtToken){
        return timeNoteService.resetTimer(timerRequestDto, jwtToken);
    }

    @PostMapping("/add")
    public ResponseEntity<Long> addTimeNote(
            @RequestBody AddTimeNoteRequestDto requestDto,
            @RequestHeader(name = HttpHeaders.AUTHORIZATION) String jwtToken){
        return timeNoteService.addTimerNote(requestDto, jwtToken);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteTimeNote(@PathVariable(name = "id") Long id, @RequestHeader(name = HttpHeaders.AUTHORIZATION) String jwtToken){
        return timeNoteService.deleteTimerNote(id, jwtToken);
    }

    @PutMapping("/update")
    public ResponseEntity<Long> updateTimeNoteText(@RequestBody TimerTextRequestDto timerTextRequestDto, @RequestHeader(name = HttpHeaders.AUTHORIZATION) String jwtToken){
        return timeNoteService.updateTimerNoteTextRequest(timerTextRequestDto, jwtToken);
    }
}
