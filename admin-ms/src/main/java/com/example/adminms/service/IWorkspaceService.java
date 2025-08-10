package com.example.adminms.service;

import com.example.adminms.model.response.WorkspaceStatisticsResponseDto;
import org.springframework.http.ResponseEntity;

public interface IWorkspaceService {
    ResponseEntity<WorkspaceStatisticsResponseDto> getUserStatistics();
}
