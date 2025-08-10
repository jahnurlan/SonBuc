package com.example.chatms.controller;

import com.example.chatms.model.dto.request.EditStatusRequestDto;
import com.example.chatms.service.IEditRequestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/edit")
public class EditRequestController {
    private final IEditRequestService editRequestService;

    @PostMapping("/reject-request")
    public ResponseEntity<String> rejectUpdatePlanRequest( @RequestBody EditStatusRequestDto editStatusRequestDto){
        log.info("Reject update plan controller is working -- requestDto=> {}",editStatusRequestDto);
        return editRequestService.rejectEditRequest(editStatusRequestDto);
    }

    @PostMapping("/accept-request")
    public ResponseEntity<String> acceptUpdatePlanRequest( @RequestBody EditStatusRequestDto editStatusRequestDto){
        log.info("Accept update plan controller is working -- requestDto=> {}",editStatusRequestDto);
        return editRequestService.acceptEditRequest(editStatusRequestDto);
    }

}





