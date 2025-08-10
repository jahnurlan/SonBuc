package com.example.planms.controller.plan;

import com.example.planms.model.dto.request.PlanItemRequestDto;
import com.example.planms.model.dto.request.PlanRequestDto;
import com.example.planms.service.IPlanItemService;
import com.example.planms.service.IPlanService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/item")
public class PlanItemController {
    private final IPlanItemService planItemService;

    @PostMapping("/save")
    public ResponseEntity<String> savePlanItem(@Valid @RequestBody PlanItemRequestDto planItemRequestDto, Principal principal){
        System.out.println("5711 controller savePlanItem => " + planItemRequestDto);
        return planItemService.savePlanItemKafkaProducer(planItemRequestDto,principal.getName());
    }
}
