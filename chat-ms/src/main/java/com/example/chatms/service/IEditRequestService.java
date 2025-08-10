package com.example.chatms.service;

import com.example.chatms.model.dto.request.EditStatusRequestDto;
import com.example.chatms.model.entity.ChatMessage;
import com.example.chatms.model.entity.PlanContainer;
import com.example.chatms.model.entity.PlanItem;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IEditRequestService {
    ChatMessage saveEditRequest(ChatMessage chatMessage, List<PlanContainer> planContainerList);

    ResponseEntity<String> rejectEditRequest( EditStatusRequestDto editStatusRequestDto);

    ResponseEntity<String> acceptEditRequest( EditStatusRequestDto editStatusRequestDto);
}

