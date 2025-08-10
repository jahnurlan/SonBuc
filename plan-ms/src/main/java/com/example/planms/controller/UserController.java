package com.example.planms.controller;

import com.example.planms.model.dto.request.InviteUserRequestDto;
import com.example.planms.model.dto.response.InvitedUserResponseDto;
import com.example.planms.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



@RestController
@RequiredArgsConstructor
@RequestMapping("/u")
public class UserController {
    private final IUserService userService;

    @GetMapping("/find-user/{username}")
    public ResponseEntity<InvitedUserResponseDto> findUser(@RequestHeader(HttpHeaders.AUTHORIZATION) String jwtToken, @PathVariable String username){
        return userService.findUser(username,jwtToken);
    }

    @PostMapping("/invite-user")
    public ResponseEntity<String> inviteUser(@RequestBody InviteUserRequestDto inviteUserRequestdto, @RequestHeader(HttpHeaders.AUTHORIZATION) String jwtToken){
        return userService.inviteUserRequest(inviteUserRequestdto,jwtToken);
    }
}


