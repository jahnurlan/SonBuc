package com.example.workspacems.service.impl;

import com.example.workspacems.model.dto.request.TaskRequestDto;
import com.example.workspacems.repository.ScheduledDayRepository;
import com.example.workspacems.repository.WorkspaceRepository;
import com.example.workspacems.service.IScheduledDayService;
import com.example.workspacems.util.Helper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScheduledDayService implements IScheduledDayService {
    private final Helper helper;
    private final WorkspaceRepository workspaceRepository;
    private final ScheduledDayRepository scheduledDayRepository;

    @Override
    @Transactional
    public ResponseEntity<String> saveScheduledDay(TaskRequestDto scheduledDayRequestDto, String jwtToken) {
//        String username = helper.getUsername(jwtToken);
//        return workspaceRepository.findByUsername(username).map(
//                workspace -> {
//                    scheduledDayRequestDto.getDayList()
//                            .forEach(day -> {
//                                ScheduledDay scheduledDay = ScheduledDay.builder()
//                                        .day(day.getDay())
//                                        .workspace(workspace)
//                                        .build();
//                                scheduledDayRepository.save(scheduledDay);
//                            });
//                    return ResponseEntity.ok("Scheduled days saved successfully.");
//                }
//        ).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Workspace not founded!"));
        return ResponseEntity.ok().body(null);
    }
}
