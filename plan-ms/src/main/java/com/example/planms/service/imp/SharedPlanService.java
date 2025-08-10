package com.example.planms.service.imp;

import com.example.planms.mapper.ModelMapper;
import com.example.planms.model.dto.request.*;
import com.example.planms.model.dto.response.PlanContainerResponseDto;
import com.example.planms.model.dto.response.SharedPlanResponseDto;
import com.example.planms.model.entity.Plan;
import com.example.planms.model.entity.PlanItem;
import com.example.planms.repository.plan.PlanRepository;
import com.example.planms.service.ISharedPlanService;
import com.example.planms.util.Helper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class SharedPlanService implements ISharedPlanService {
    private final PlanRepository planRepository;
    private final ModelMapper modelMapper;
    private final Helper helper;

    @Override
    public ResponseEntity<List<PlanContainerResponseDto>> getUpdatedPlanItems(String secondUsername, PlanRequestDto requestDto, String jwtToken) {
        String username = helper.getUsername(jwtToken);
        log.info("Fetching updated plan items for user: {} and second user: {}", username, secondUsername);

        Plan plan = planRepository.findPlanByUsernameAndPlanDate(username, requestDto.getPlanDate())
                .orElseThrow(() -> {
                    log.warn("Plan not found for user: {} on date: {}", username, requestDto.getPlanDate());
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Plan not found");
                });

        Plan secondPlan = plan.getConnectedPlans().stream()
                .filter(p -> p.getUsername().equals(secondUsername))
                .findFirst()
                .orElseThrow(() -> {
                    log.warn("No connected plan found for second user: {}", secondUsername);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Connected plan not found");
                });

        log.info("Connected plan found for second user: {}. Checking for updates...", secondUsername);

        Map<Integer, PlanContainerRequestDto> planRequestContainerMap = requestDto.getPlanContainerRequestDtoList().stream()
                .collect(Collectors.toMap(
                        PlanContainerRequestDto::getIndex,
                        Function.identity(),
                        (existing, replacement) -> existing
                ));

        List<PlanContainerResponseDto> updatedPlanContainers = secondPlan.getPlanContainerList().stream()
                .map(planContainer -> {
                    PlanContainerRequestDto planContainerRequestDto = planRequestContainerMap.get(planContainer.getIndex());

                    if (planContainerRequestDto == null){
                        log.info("Fitted planContainerRequestDto item is empty..");
                        return PlanContainerResponseDto.builder()
                                .id(planContainer.getId())
                                .index(planContainer.getIndex())
                                .name(planContainer.getName())
                                .planItemList(planContainer.getPlanItemList())
                                .build();
                    }

                    List<PlanItem> updatedPlanItems = checkUpdatedPlanItems(
                            planContainer.getPlanItemList(),
                            planContainerRequestDto.getPlanItemList(),
                            username
                    );

                    if (planContainer.getName().equals(planContainer.getName().trim()) && updatedPlanItems.isEmpty()) {
                        return null;
                    }

                    return PlanContainerResponseDto.builder()
                            .id(planContainer.getId())
                            .index(planContainer.getIndex())
                            .name(planContainer.getName())
                            .planItemList(updatedPlanItems)
                            .build();
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        log.warn("Updated plan containers ====> {}",updatedPlanContainers);
        return ResponseEntity.ok(updatedPlanContainers);
    }

    private List<PlanItem> checkUpdatedPlanItems(List<PlanItem> originalPlanItemList, List<PlanItemRequestDto> requestPlanItemList, String username) {
        log.debug("Checking updated plan items for user: {}", username);

        Map<Integer, PlanItemRequestDto> requestedPlanItemsMap = requestPlanItemList.stream()
                .collect(Collectors.toMap(
                        PlanItemRequestDto::getIndex,
                        item -> item,
                        (existing, replacement) -> existing
                ));

        List<PlanItem> updatedPlanItems = originalPlanItemList.stream()
                .filter(planItem -> Optional.ofNullable(requestedPlanItemsMap.get(planItem.getIndex()))
                        .map(reqItem -> !Objects.equals(planItem.getText(), reqItem.getText()) ||
                                !Objects.equals(planItem.getStatus(), reqItem.getStatus()))
                        .orElse(true))
                .peek(planItem -> log.info("PlanItem updated - Index: {}", planItem.getIndex()))
                .collect(Collectors.toList());

        log.debug("Total updated items after filtering: {}", updatedPlanItems.size());
        return updatedPlanItems;
    }


    @Override
    public ResponseEntity<List<SharedPlanResponseDto>> getSharedPlanHistory(String friendUsername, String jwtToken) {
        String username = helper.getUsername(jwtToken);

        List<Plan> sharedPlansByUsernames = planRepository.findSharedPlansByUsernames(username, friendUsername);
        List<SharedPlanResponseDto> sharedPlans = sharedPlansByUsernames.stream()
                .map((modelMapper::planToSharedPlanHistoryResponseDto))
                .collect(Collectors.toList());

        return ResponseEntity.ok().body(sharedPlans);
    }
}
