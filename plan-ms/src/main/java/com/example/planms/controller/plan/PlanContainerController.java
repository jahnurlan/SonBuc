package com.example.planms.controller.plan;

import com.example.planms.model.dto.request.KafkaContainerNameUpdateRequest;
import com.example.planms.model.dto.request.PlanContainerRequestDto;
import com.example.planms.model.dto.request.PlanItemRequestDto;
import com.example.planms.service.IPlanContainerService;
import com.example.planms.service.IPlanItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.security.Principal;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/container")
public class PlanContainerController {
    private final IPlanContainerService planContainerService;

    @PostMapping("/save")
    public ResponseEntity<String> savePlanContainer(@Valid @RequestBody KafkaContainerNameUpdateRequest containerRequestDto, Principal principal){
        System.out.println("5711 controller savePlanContainer => " + containerRequestDto);
        return planContainerService.updateContainerNameKafkaProducer(containerRequestDto,principal.getName());
    }
}
