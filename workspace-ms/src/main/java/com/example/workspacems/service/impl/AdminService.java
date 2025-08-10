package com.example.workspacems.service.impl;

import com.example.workspacems.model.dto.response.StatisticsResponseDto;
import com.example.workspacems.repository.WorkspaceRepository;
import com.example.workspacems.service.IAdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminService implements IAdminService {
    private final WorkspaceRepository workspaceRepository;

    @Override
    public ResponseEntity<StatisticsResponseDto> getUserStatistics() {
        long countAllWorkspaces = workspaceRepository.countAllWorkspaces();
        long countTodayAllWorkspaces = workspaceRepository.countWorkspacesCreatedToday(LocalDate.now());

        StatisticsResponseDto statisticsResponseDto = StatisticsResponseDto.builder()
                .countAllWorkspaces(countAllWorkspaces)
                .countTodayAllWorkspaces(countTodayAllWorkspaces)
                .build();
        return ResponseEntity.ok().body(statisticsResponseDto);
    }
}
