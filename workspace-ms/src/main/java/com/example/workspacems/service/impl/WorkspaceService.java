package com.example.workspacems.service.impl;

import com.example.workspacems.model.dto.request.DayRequestDto;
import com.example.workspacems.model.dto.request.TaskRequestDto;
import com.example.workspacems.model.dto.request.WorkspaceRequestDto;
import com.example.workspacems.model.entity.ScheduledDay;
import com.example.workspacems.model.entity.ScheduledDayTask;
import com.example.workspacems.model.entity.Workspace;
import com.example.workspacems.model.entity.WorkspaceTask;
import com.example.workspacems.repository.ScheduledDayRepository;
import com.example.workspacems.repository.ScheduledTaskRepository;
import com.example.workspacems.repository.WorkspaceRepository;
import com.example.workspacems.repository.WorkspaceTaskRepository;
import com.example.workspacems.service.IWorkspaceService;
import com.example.workspacems.util.Helper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class WorkspaceService implements IWorkspaceService {
    private final Helper helper;
    private final WorkspaceRepository workspaceRepository;
    private final ScheduledDayRepository scheduledDayRepository;

    @Override
    public ResponseEntity<List<Workspace>> getAllWorkspaces(String jwtToken) {
        String username = helper.getUsername(jwtToken);
        List<Workspace> workspaceList = workspaceRepository.findAllByUsername(username);
        return workspaceList.isEmpty() ?
                ResponseEntity.status(HttpStatus.NOT_FOUND).body(null) :
                ResponseEntity.ok().body(workspaceList);
    }

    @Override
    @Transactional
    public ResponseEntity<String> saveWorkspace(WorkspaceRequestDto requestDto, String jwtToken) {
        String username = helper.getUsername(jwtToken);
        log.info("saveWorkspace çağrıldı. Kullanıcı: {}, Workspace ID: {}", username, requestDto.getId());

        Workspace workspace;
        boolean isNew = false;

        if (requestDto.getId() == null) {
            isNew = true;
            log.info("Yeni workspace oluşturuluyor...");

            Workspace newWorkspace = Workspace.builder()
                    .username(username)
                    .title(requestDto.getTitle())
                    .description(requestDto.getDescription())
                    .type(requestDto.getScheduleType())
                    .startTime(requestDto.getStartTime())
                    .endTime(requestDto.getEndTime())
                    .build();

            // Workspace-level task ekleme
            if (requestDto.getTaskRequestDtoList() != null) {
                log.info("Workspace seviyesinde {} task eklenecek.", requestDto.getTaskRequestDtoList().size());
                List<WorkspaceTask> tasks = requestDto.getTaskRequestDtoList().stream()
                        .filter(dto -> !dto.getText().isEmpty())
                        .map(dto -> WorkspaceTask.builder().text(dto.getText()).build())
                        .collect(Collectors.toList());
                tasks.forEach(task -> task.setWorkspace(newWorkspace));
                newWorkspace.setTasks(tasks);
            }

            // Günler ve gün task'ları
            if (requestDto.getDayList() != null) {
                log.info("{} adet gün eklenecek.", requestDto.getDayList().size());
                List<ScheduledDay> days = new ArrayList<>();
                for (DayRequestDto dayDto : requestDto.getDayList()) {
                    ScheduledDay day = ScheduledDay.builder()
                            .day(dayDto.getDay())
                            .description(dayDto.getDescription())
                            .startTime(dayDto.getStartTime())
                            .endTime(dayDto.getEndTime())
                            .workspace(newWorkspace)
                            .build();

                    if (dayDto.getTaskRequestDtoList() != null) {
                        log.info("Gün: {}, {} adet task içeriyor.", dayDto.getDay(), dayDto.getTaskRequestDtoList().size());
                        List<ScheduledDayTask> dayTasks = dayDto.getTaskRequestDtoList().stream()
                                .filter(dto -> !dto.getText().isEmpty())
                                .map(dto -> ScheduledDayTask.builder().text(dto.getText()).build())
                                .collect(Collectors.toList());
                        dayTasks.forEach(task -> task.setScheduledDay(day));
                        day.setTasks(dayTasks);
                    }

                    days.add(day);
                }
                newWorkspace.setScheduledDays(days);
            }

            workspace = workspaceRepository.save(newWorkspace);
            log.info("Yeni workspace başarıyla kaydedildi. ID: {}", workspace.getId());

        } else {
            log.info("Mevcut workspace güncelleniyor. ID: {}", requestDto.getId());

            workspace = workspaceRepository.findById(requestDto.getId())
                    .orElseThrow(() -> {
                        log.error("Workspace bulunamadı. ID: {}", requestDto.getId());
                        return new RuntimeException("Workspace not found");
                    });

            if (!workspace.getUsername().equals(username)) {
                log.warn("Yetkisiz erişim! Kullanıcı: {}, Workspace sahibi: {}", username, workspace.getUsername());
                throw new SecurityException("Unauthorized access to workspace");
            }

            workspace.setTitle(requestDto.getTitle());
            workspace.setDescription(requestDto.getDescription());
            workspace.setStartTime(requestDto.getStartTime());
            workspace.setEndTime(requestDto.getEndTime());

            workspace.getTasks().clear();
            if (requestDto.getTaskRequestDtoList() != null) {
                log.info("Workspace task'ları güncelleniyor. Yeni task sayısı: {}", requestDto.getTaskRequestDtoList().size());
                for (TaskRequestDto taskDto : requestDto.getTaskRequestDtoList()) {
                    if (!taskDto.getText().isEmpty()) {
                        workspace.getTasks().add(WorkspaceTask.builder()
                                .text(taskDto.getText())
                                .workspace(workspace)
                                .build());
                    }
                }
            }

            log.info("Eski günler siliniyor...");
            scheduledDayRepository.deleteAll(workspace.getScheduledDays());
            workspace.getScheduledDays().clear();

            if (requestDto.getDayList() != null) {
                log.info("Yeni günler ekleniyor. Gün sayısı: {}", requestDto.getDayList().size());
                for (DayRequestDto dayDto : requestDto.getDayList()) {
                    ScheduledDay day = ScheduledDay.builder()
                            .day(dayDto.getDay())
                            .description(dayDto.getDescription())
                            .startTime(dayDto.getStartTime())
                            .endTime(dayDto.getEndTime())
                            .workspace(workspace)
                            .build();

                    if (dayDto.getTaskRequestDtoList() != null) {
                        log.info("Gün: {}, Task sayısı: {}", dayDto.getDay(), dayDto.getTaskRequestDtoList().size());
                        for (TaskRequestDto taskDto : dayDto.getTaskRequestDtoList()) {
                            if (!taskDto.getText().isEmpty()) {
                                day.getTasks().add(ScheduledDayTask.builder()
                                        .text(taskDto.getText())
                                        .scheduledDay(day)
                                        .build());
                            }
                        }
                    }

                    workspace.getScheduledDays().add(day);
                }
            }

            workspaceRepository.save(workspace);
            log.info("Workspace başarıyla güncellendi. ID: {}", workspace.getId());
        }

        return isNew
                ? ResponseEntity.ok(workspace.getId().toString())
                : ResponseEntity.noContent().build();
    }

}
