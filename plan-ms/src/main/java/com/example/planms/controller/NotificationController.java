package com.example.planms.controller;

import com.example.planms.model.dto.request.InviteStatusRequestDto;
import com.example.planms.model.dto.response.InviteAnnounceResponseDto;
import com.example.planms.service.IAnnounceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/announce")
public class NotificationController {
    private final IAnnounceService announceService;

    @GetMapping("/invite-count")
    public ResponseEntity<Long> getInviteAnnounceCount(@RequestHeader(HttpHeaders.AUTHORIZATION) String jwtToken){
        return announceService.getInviteAnnounceCount(jwtToken);
    }

    @GetMapping("/all-invite")
    public ResponseEntity<List<InviteAnnounceResponseDto>> getAllInviteAnnounce(@RequestHeader(HttpHeaders.AUTHORIZATION) String jwtToken){
        return announceService.getAllInviteAnnounce(jwtToken);
    }

    @PostMapping("/reject-invite")
    public void rejectInviteRequest(@RequestBody InviteStatusRequestDto inviteStatusRequestDto, @RequestHeader(HttpHeaders.AUTHORIZATION) String jwtToken){
        announceService.rejectInviteRequest(inviteStatusRequestDto,jwtToken);
    }

    @PostMapping("/accept-invite")
    public void acceptInviteRequest(@RequestBody InviteStatusRequestDto inviteStatusRequestDto, @RequestHeader(HttpHeaders.AUTHORIZATION) String jwtToken){
        announceService.acceptInviteRequest(inviteStatusRequestDto,jwtToken);
    }
}


