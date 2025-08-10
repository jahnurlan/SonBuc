package com.example.workspacems.service;

import com.example.workspacems.model.dto.request.WorkspaceRequestDto;
import com.example.workspacems.model.entity.Workspace;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IWorkspaceService {

    ResponseEntity<String> saveWorkspace(WorkspaceRequestDto workspaceRequestDto, String jwtToken);

    ResponseEntity<List<Workspace>> getAllWorkspaces(String jwtToken);
}
