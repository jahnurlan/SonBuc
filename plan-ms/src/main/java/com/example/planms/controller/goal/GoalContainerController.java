package com.example.planms.controller.goal;

import com.example.planms.model.dto.request.KafkaContainerNameUpdateRequest;
import com.example.planms.service.IGoalContainerService;
import com.example.planms.service.IPlanContainerService;
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
@RequestMapping("/goal/container")
public class GoalContainerController {
    private final IGoalContainerService goalContainerService;

    @PostMapping("/save")
    public ResponseEntity<String> saveGoalPlanContainer(@Valid @RequestBody KafkaContainerNameUpdateRequest containerRequestDto, Principal principal){
        System.out.println("5711 controller savePlanContainer => " + containerRequestDto);
        return goalContainerService.updateContainerNameKafkaProducer(containerRequestDto,principal.getName());
    }
}
