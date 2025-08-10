package com.example.timerms.controller;

import com.example.timerms.model.dto.request.TimerConnectRequestDto;
import com.example.timerms.service.ITimerNoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;


@RestController
@RequiredArgsConstructor
@RequestMapping("/connect")
public class TimeNoteConnectionController {
    private final ITimerNoteService timeNoteService;

    @PostMapping
    public ResponseEntity<Long> connectTimeNote(@RequestBody TimerConnectRequestDto timerRequestDto, Principal principal){
        return timeNoteService.connectTimerNoteToPlan(timerRequestDto, principal.getName());
    }

}
