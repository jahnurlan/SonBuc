package com.example.planms.service;

import com.example.planms.model.dto.request.*;
import org.springframework.http.ResponseEntity;

public interface IPlanItemService {

    ResponseEntity<String> savePlanItemKafkaProducer(PlanItemRequestDto planRequestDto,String username);
    void savePlanItemKafkaConsumer(KafkaRequest kafkaRequest);
}
