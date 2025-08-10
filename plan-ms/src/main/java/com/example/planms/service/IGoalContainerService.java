package com.example.planms.service;

import com.example.planms.model.dto.request.KafkaContainerNameUpdateRequest;
import com.example.planms.model.dto.request.KafkaRequest;
import org.springframework.http.ResponseEntity;

public interface IGoalContainerService {
    ResponseEntity<String> updateContainerNameKafkaProducer(KafkaContainerNameUpdateRequest request, String username);
    void updateContainerNameKafkaConsumer(KafkaRequest request);
}
