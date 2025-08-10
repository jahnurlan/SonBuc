package com.example.planms.service;

import com.example.planms.model.dto.request.InviteStatusRequestDto;
import com.example.planms.model.dto.request.KafkaRequest;
import com.example.planms.model.dto.response.InviteAnnounceResponseDto;
import com.example.planms.model.entity.InviteAnnounce;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IAnnounceService {

    ResponseEntity<Long> getInviteAnnounceCount(String jwtToken);

    ResponseEntity<List<InviteAnnounceResponseDto>> getAllInviteAnnounce(String jwtToken);
    void updateInviteAnnounceReadingStatus(List<Long> ids);

    void rejectInviteRequest(InviteStatusRequestDto inviteStatusRequestDto, String jwtToken);

    void acceptInviteRequest(InviteStatusRequestDto inviteStatusRequestDto, String jwtToken);

    void createSharedPlan(KafkaRequest request);
}
