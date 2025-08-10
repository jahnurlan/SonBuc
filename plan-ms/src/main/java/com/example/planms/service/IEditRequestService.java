package com.example.planms.service;

import com.example.planms.model.dto.request.KafkaRequest;

public interface IEditRequestService {
    void updatePlanText(KafkaRequest request);
}

