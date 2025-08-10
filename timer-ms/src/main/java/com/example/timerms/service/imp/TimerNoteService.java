package com.example.timerms.service.imp;

import com.example.timerms.model.dto.request.*;
import com.example.timerms.model.entity.TimeNote;
import com.example.timerms.repository.TimeNoteRepository;
import com.example.timerms.service.ITimerNoteService;
import com.example.timerms.util.Helper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class TimerNoteService implements ITimerNoteService {
    private final Helper helper;
    private final TimeNoteRepository timeNoteRepository;
    private final KafkaTemplate<String, KafkaRequest> kafkaTemplate;

    @Override
    public ResponseEntity<Long> addTimerNote(AddTimeNoteRequestDto requestDto, String jwtToken) {
        String username = helper.getUsername(jwtToken);
        log.info("addTimerNote is working for ==> {}", username);
        TimeNote timeNote = createAndSaveTimeNote(username, requestDto.getCreatingTime());

        return ResponseEntity.ok().body(timeNote.getId());
    }

    @Override
    public ResponseEntity<String> deleteTimerNote(Long id, String jwtToken) {
        String username = helper.getUsername(jwtToken);
        return timeNoteRepository.findById(id)
                .map(timeNote -> {
                    if (!timeNote.getUsername().equals(username)) {
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                .body("Unauthorized to delete this TimeNote.");
                    }
                    if (timeNote.getContainerIndex() != null){
                        sendKafkaDeletePlanConnection(timeNote.getUsername(), timeNote.getContainerIndex());
                    }
                    timeNoteRepository.delete(timeNote);
                    return ResponseEntity.ok().body("TimeNote deleted successfully.");
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("TimeNote not found with ID: " + id));
    }

    private void sendKafkaDeletePlanConnection(String username, Long containerIndex) {
        KafkaRequest kafkaRequest = KafkaRequest.builder()
                .username(username)
                .containerIndex(containerIndex)
                .build();

        log.info("kafkaRequest => {}", kafkaRequest.toString());
        kafkaTemplate.send("delete-plan-connection-topic", kafkaRequest);
    }

    @Override
    public ResponseEntity<Long> updateTimerNoteTextRequest(TimerTextRequestDto timerTextRequestDto, String jwtToken) {
        String username = helper.getUsername(jwtToken);

        log.info("[updateTimerNoteTextRequest] Kullanıcı: {}, Gelen ID: {}, Not: {}",
                username,
                timerTextRequestDto.getTimeNoteId(),
                timerTextRequestDto.getNote());

        if (timerTextRequestDto.getTimeNoteId() == 1) {
            TimeNote timeNote = TimeNote.builder()
                    .username(username)
                    .note(timerTextRequestDto.getNote())
                    .day(LocalDate.now())
                    .build();

            TimeNote saved = timeNoteRepository.save(timeNote);

            log.info("[updateTimerNoteTextRequest] Yeni TimeNote oluşturuldu. ID: {}, Tarih: {}",
                    saved.getId(), saved.getDay());

            return ResponseEntity.status(HttpStatus.CREATED).body(saved.getId());
        } else {
            KafkaRequest kafkaRequest = KafkaRequest.builder()
                    .username(username)
                    .timeNoteId(timerTextRequestDto.getTimeNoteId())
                    .note(timerTextRequestDto.getNote())
                    .build();

            log.info("[updateTimerNoteTextRequest] Kafka mesajı gönderiliyor. Topic: timer-note-text-topic, Payload: {}",
                    kafkaRequest);

            kafkaTemplate.send("timer-note-text-topic", kafkaRequest);

            return ResponseEntity.ok(null);
        }
    }

    @Override
    @Transactional
    public void updateTimerNoteText(KafkaRequest request) {
        timeNoteRepository.findById(request.getTimeNoteId())
                .ifPresentOrElse(timeNote -> {
                    if (!timeNote.getUsername().equals(request.getUsername())) {
                        log.error("Unauthorized to update this TimeNote.");
                    } else {
                        timeNote.setNote(request.getNote());
                        timeNoteRepository.save(timeNote);
                        log.info("TimeNote text updated successfully.");
                    }
                }, () -> log.error("TimeNote not found with ID: {}", request.getTimeNoteId()));
    }

    private TimeNote createAndSaveTimeNote(String username,OffsetDateTime creatingTime) {
        TimeNote timeNote = TimeNote.builder()
                .username(username)
                .creatingTime(creatingTime)
                .day(LocalDate.now())
                .build();
        log.info("Time note created and saved ==> {} || {} || {}", timeNote.getUsername(), timeNote.getCreatingTime(), timeNote.getDay());
        return timeNoteRepository.save(timeNote);
    }

    @Override
    @Transactional
    public ResponseEntity<Long> connectTimerNoteToPlan(TimerConnectRequestDto requestDto, String username) {
        log.info("Timer bağlantı isteği alındı - username: {}, request: {}", username, requestDto);

        Optional<TimeNote> optionalTimeNote = timeNoteRepository.findById(requestDto.getTimerNoteId());
        TimeNote timeNote;

        if (optionalTimeNote.isPresent()) {
            timeNote = optionalTimeNote.get();

            if (!timeNote.getUsername().equals(username)) {
                log.warn("Yetkisiz erişim - timeNote.username: {}, request.username: {}", timeNote.getUsername(), username);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }

            timeNote.setContainerIndex(requestDto.getPlanContainerIndex());
            log.info("Mevcut TimeNote güncellendi - id: {}, containerIndex: {}",
                    timeNote.getId(), timeNote.getContainerIndex());

        } else {
            timeNote = TimeNote.builder()
                    .username(username)
                    .day(LocalDate.now())
                    .containerIndex(requestDto.getPlanContainerIndex())
                    .build();

            log.info("Yeni TimeNote oluşturuldu - username: {}, containerIndex: {}", username, timeNote.getContainerIndex());
        }

        TimeNote savedTimeNote = timeNoteRepository.save(timeNote);
        sendKafkaTimeConnectRequest(username, requestDto.getPlanDate(), requestDto.getPlanContainerIndex(), savedTimeNote.getId());

        return ResponseEntity.ok(savedTimeNote.getId());
    }


    public void sendKafkaTimeConnectRequest(String username, LocalDate planDate, Long containerIndex, Long timeNoteId){
        KafkaRequest kafkaRequest = KafkaRequest.builder()
                .username(username)
                .planDate(planDate)
                .containerIndex(containerIndex)
                .timeNoteId(timeNoteId)
                .build();

        log.info("sendKafkaTimeConnectRequest => {}", kafkaRequest.toString());
        kafkaTemplate.send("timer-note-connect-topic", kafkaRequest);
    }

    public ResponseEntity<List<TimeNote>> getAllTimeNotes(LocalDate day, String jwtToken) {
        log.info("Fetching all time notes for day: {}", day);
        String username = helper.getUsername(jwtToken);

        return ResponseEntity.ok().body(timeNoteRepository.findAllByUsernameAndDay(username, day));
    }

    @Override
    @Transactional
    public ResponseEntity<Long> startTimer(TimerRequestDto requestDto, String jwtToken) {
        log.info("Starting timer for user with request: {}", requestDto);
        String username = helper.getUsername(jwtToken);

        TimeNote timeNote;
        if(requestDto.getTimeNoteId() == 1){
            timeNote = createAndSaveTimeNote(username, requestDto.getStartTime());
        } else {
            timeNote = timeNoteRepository.findById(requestDto.getTimeNoteId())
                    .orElseGet(() -> {
                        log.warn("TimeNote not found with ID: {}. Creating new one.", requestDto.getTimeNoteId());
                        return createAndSaveTimeNote(username, requestDto.getStartTime());
                    });
        }

        if ("pause".equals(timeNote.getTimerStatus())) {
            long newDurationInSeconds = helper.calculateNewDuration(timeNote.getStartTime(), timeNote.getStopTime(), timeNote.getDuration());
            timeNote.setDuration(LocalTime.ofSecondOfDay(newDurationInSeconds));
            log.info("TimeNote for user '{}' on {} - Updated Duration: {}",
                    timeNote.getUsername(), timeNote.getCreatingTime().toLocalDate(),
                    helper.formatDuration(timeNote.getDuration()));
        }

        timeNote.setStartTime(requestDto.getStartTime().withOffsetSameInstant(ZoneOffset.UTC));
        timeNote.setStopTime(null);
        timeNote.setTimerStatus("play");

        log.info("TimeNote for user '{}' on {} - Timer started at {}",
                timeNote.getUsername(), timeNote.getCreatingTime().toLocalDate(),
                helper.formatDateTime(timeNote.getStartTime()));

        timeNoteRepository.save(timeNote);
        return ResponseEntity.ok().body(timeNote.getId());
    }


    @Override
    @Transactional
    public ResponseEntity<String> stopTimer(TimerRequestDto requestDto, String jwtToken) {
        String username = helper.getUsername(jwtToken);

        TimeNote timeNote = timeNoteRepository.findById(requestDto.getTimeNoteId())
                .orElseGet(() -> {
                    log.warn("TimeNote not found with ID: {}. Creating new one.", requestDto.getTimeNoteId());
                    return createAndSaveTimeNote(username, requestDto.getStopTime());
                });

        timeNote.setStopTime(requestDto.getStopTime().withOffsetSameInstant(ZoneOffset.UTC));
        timeNote.setTimerStatus("pause");
        timeNoteRepository.save(timeNote);

        return ResponseEntity.ok().body("TimeNote stopped successfully.");
    }

    @Override
    @Transactional
    public ResponseEntity<String> resetTimer(TimerRequestDto requestDto, String jwtToken) {
        log.info("Resetting timer for user with request: {}", requestDto);
        String username = helper.getUsername(jwtToken);

        TimeNote timeNote = timeNoteRepository.findById(requestDto.getTimeNoteId())
                .orElseGet(() -> {
                    log.warn("TimeNote not found with ID: {}. Creating new one.", requestDto.getTimeNoteId());
                    return createAndSaveTimeNote(username, OffsetDateTime.now(ZoneOffset.UTC));
                });

        timeNote.setStartTime(null);
        timeNote.setStopTime(null);
        timeNote.setDuration(null);
        timeNote.setTimerStatus("reset");

        timeNoteRepository.save(timeNote);
        log.info("TimeNote reset successfully.");
        return ResponseEntity.ok().body("TimeNote reset successfully.");
    }
}

