package com.example.userms.controller;

import com.example.userms.model.dto.request.*;
import com.example.userms.model.dto.response.AuthenticationResponse;
import com.example.userms.model.dto.response.UserResponseDto;
import com.example.userms.model.entity.User;
import com.example.userms.service.IAdminService;
import com.example.userms.service.IGuestService;
import com.example.userms.service.IUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;

@RestController
@Slf4j
@RequiredArgsConstructor
public class UserController {
    private final IUserService userService;
    private final IGuestService guestService;
    private final IAdminService adminService;

    @GetMapping("/info")
    public ResponseEntity<UserResponseDto> getUserInfo(Principal principal){
        if (principal == null) {
            log.warn("Principal is null!");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        } else {
            log.info("Principal username ==> {}", principal.getName());
        }

        return userService.getUser(principal.getName());
    }

    @PostMapping("/auth/registration")
    public ResponseEntity<String> registration(@RequestBody UserRequestDto userRequestDto){
        return userService.saveUser(userRequestDto);
    }

    @PostMapping("/auth/send/l")
    public ResponseEntity<String> sendConfirmationLink(@RequestBody EmailRequestDto emailRequestDto){
        return userService.sendConfirmationLinkToUser(emailRequestDto);
    }

    @PostMapping("/auth/authentication")
    public ResponseEntity<AuthenticationResponse> authentication(@RequestBody AuthenticationRequest request){
        return userService.authenticateUser(request);
    }

    @GetMapping("/auth/confirmation/{token}")
    public ResponseEntity<String> confirmation(@PathVariable String token){
        return userService.confirmAccount(token);
    }

    //-----Reset Password------
    @PostMapping("/auth/reset-password/check-email")
    public ResponseEntity<String> checkEmail(@Valid @RequestBody EmailRequestDto emailRequestDto){
        return userService.checkEmailInDatabase(emailRequestDto.getEmail());
    }

    @PostMapping("/auth/reset-password/submit/{token}")
    public ResponseEntity<String> resetsPassword(@PathVariable String token, @Valid @RequestBody PasswordRequestDto passwordRequestDto){
        return userService.resetsPassword(token, passwordRequestDto);
    }

    @PostMapping("/auth/reset-password/check/{token}")
    public ResponseEntity<String> checkResetToken(@PathVariable String token){
        return userService.checkResetToken(token);
    }
    //---------------------------

    //-----Renew Password------
    @PostMapping("/auth/send-otp")
    public ResponseEntity<String> sendOtpMail(@RequestHeader String token){
        return userService.sendOTP(token);
    }

    @PostMapping("/auth/check-otp/{username}")
    public ResponseEntity<String> checkOtp(@PathVariable String username,@RequestBody String otp){
        return userService.checkOtp(username,otp);
    }

    @PostMapping("/auth/guest")
    public ResponseEntity<AuthenticationResponse> loginAsGuest() {
        return ResponseEntity.ok(guestService.generateGuestToken());
    }

    @PostMapping("/auth/admin")
    public ResponseEntity<AuthenticationResponse> loginAsAdmin(@RequestBody AdminLoginRequestDto adminLoginRequestDto) {
        return adminService.generateAdminToken(adminLoginRequestDto);
    }
}
