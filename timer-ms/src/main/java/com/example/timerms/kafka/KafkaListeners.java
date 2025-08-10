package com.example.timerms.kafka;

import com.example.timerms.model.dto.request.KafkaRequest;
import com.example.timerms.service.ITimerNoteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Slf4j
@Service
public class KafkaListeners {
    private final ITimerNoteService timerNoteService;

    @KafkaListener(topics = "timer-note-text-topic",groupId = "groupId")
    void timerNoteTextUpdateListener(KafkaRequest request) {
        log.info("Kafka Listener => update timerNoteTextUpdateListener text is working 571");
        timerNoteService.updateTimerNoteText(request);
    }
}
