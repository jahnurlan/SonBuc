package com.example.planms.controller;

import com.example.planms.model.dto.request.EditTextRequestDto;
import com.example.planms.service.IEditRequestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/crud")
public class EditRequestController {
    private final IEditRequestService editRequestService;

//    @PutMapping("/update-text")
//    public void updatePlanText(@RequestHeader(HttpHeaders.AUTHORIZATION) String jwtToken, @RequestBody EditTextRequestDto editTextRequestDto){
//        log.info("Update plan text controller is working = token=> {}\n requestDto=> {}",jwtToken,editTextRequestDto);
//        editRequestService.updatePlanText(editTextRequestDto);
//    }

}
