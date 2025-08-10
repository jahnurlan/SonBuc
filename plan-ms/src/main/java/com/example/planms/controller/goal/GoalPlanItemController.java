package com.example.planms.controller.goal;

import com.example.planms.model.dto.request.PlanItemRequestDto;
import com.example.planms.service.IGoalPlanItemService;
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
@RequestMapping("/goal/item")
public class GoalPlanItemController {
    private final IGoalPlanItemService goalPlanItemService;

    @PostMapping("/save")
    public ResponseEntity<String> saveGoalPlanItem(@Valid @RequestBody PlanItemRequestDto planItemRequestDto, Principal principal){
        System.out.println("5711 controller savePlanItem => " + planItemRequestDto);
        return goalPlanItemService.savePlanItemKafkaProducer(planItemRequestDto,principal.getName());
    }
}
