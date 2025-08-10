package com.example.planms.service;

import com.example.planms.model.dto.request.KafkaRequest;
import com.example.planms.model.dto.request.PlanItemRequestDto;
import org.springframework.http.ResponseEntity;

public interface IGoalPlanItemService {

    ResponseEntity<String> savePlanItemKafkaProducer(PlanItemRequestDto planRequestDto,String username);
    void savePlanItemKafkaConsumer(KafkaRequest kafkaRequest);
}
