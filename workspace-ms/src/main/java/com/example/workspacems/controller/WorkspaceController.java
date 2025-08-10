package com.example.workspacems.controller;

import com.example.workspacems.model.dto.request.WorkspaceRequestDto;
import com.example.workspacems.model.entity.Workspace;
import com.example.workspacems.service.IWorkspaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/crud")
public class WorkspaceController {
    private final IWorkspaceService workspaceService;

    @PostMapping("/save")
    public ResponseEntity<String> saveWorkspace(
            @RequestBody WorkspaceRequestDto workspaceRequestDto,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String jwtToken
    ){
        return workspaceService.saveWorkspace(workspaceRequestDto, jwtToken);
    }

    @GetMapping("/get")
    public ResponseEntity<List<Workspace>> getAllWorkspace(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String jwtToken
    ){
        return workspaceService.getAllWorkspaces(jwtToken);
    }
}
