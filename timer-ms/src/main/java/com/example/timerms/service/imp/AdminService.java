package com.example.timerms.service.imp;

import com.example.timerms.model.dto.response.StatisticsResponseDto;
import com.example.timerms.repository.TimeNoteRepository;
import com.example.timerms.service.IAdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminService implements IAdminService {
    private final TimeNoteRepository timeNoteRepository;

    @Override
    public ResponseEntity<StatisticsResponseDto> getUserStatistics() {
        long countAllTimeNotes = timeNoteRepository.countAllTimeNotes();
        long countTodayAllTimeNotes = timeNoteRepository.countTimeNotesByDay(LocalDate.now());

        StatisticsResponseDto statisticsResponseDto = StatisticsResponseDto.builder()
                .countAllTimeNotes(countAllTimeNotes)
                .countTodayAllTimeNotes(countTodayAllTimeNotes)
                .build();
        return ResponseEntity.ok().body(statisticsResponseDto);
    }
}
