package com.example.planms.service;

import com.example.planms.model.dto.request.InviteUserRequestDto;
import com.example.planms.model.dto.request.KafkaRequest;
import com.example.planms.model.dto.response.InvitedUserResponseDto;
import org.springframework.http.ResponseEntity;

public interface IUserService {

    ResponseEntity<InvitedUserResponseDto> findUser(String username,String jwtToken);

    ResponseEntity<String> inviteUserRequest(InviteUserRequestDto inviteUserRequestDto, String jwtToken);

    void inviteUser(KafkaRequest request);
}
