package com.example.adminms.service.impl;

import com.example.adminms.feigns.WorkspaceServiceFeignClient;
import com.example.adminms.model.response.WorkspaceStatisticsResponseDto;
import com.example.adminms.service.IPlanService;
import com.example.adminms.service.IWorkspaceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class WorkspaceService implements IWorkspaceService {
    private final WorkspaceServiceFeignClient workspaceServiceFeignClient;

    @Override
    public ResponseEntity<WorkspaceStatisticsResponseDto> getUserStatistics() {
        return ResponseEntity.ok(workspaceServiceFeignClient.getUserStatistics());
    }
}
