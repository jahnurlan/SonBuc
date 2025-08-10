package com.example.planms.service.imp.goal;

import com.example.planms.mapper.ModelMapper;
import com.example.planms.model.dto.request.GoalsRequestDto;
import com.example.planms.model.dto.request.KafkaRequest;
import com.example.planms.model.dto.request.PlanContainerRequestDto;
import com.example.planms.model.dto.request.PlanStatusUpdateRequestDto;
import com.example.planms.model.dto.response.connect_goal.GoalResponseDtoForConnect;
import com.example.planms.model.entity.*;
import com.example.planms.model.enums.GoalType;
import com.example.planms.repository.goal.GoalContainerRepository;
import com.example.planms.repository.goal.GoalItemRepository;
import com.example.planms.repository.goal.GoalPlanItemRepository;
import com.example.planms.repository.goal.GoalRepository;
import com.example.planms.repository.plan.PlanItemRepository;
import com.example.planms.repository.plan.PlanRepository;
import com.example.planms.service.IGoalService;
import com.example.planms.util.Helper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class GoalService implements IGoalService {
    private final PlanRepository planRepository;
    private final GoalRepository goalRepository;
    private final GoalItemRepository goalItemRepository;
    private final GoalContainerRepository goalContainerRepository;
    private final GoalPlanItemRepository goalPlanItemRepository;
    private final PlanItemRepository planItemRepository;
    private final Helper helper;
    private final ModelMapper mapper;
    private final KafkaTemplate<String, KafkaRequest> kafkaTemplate;

    @Override
    @Transactional
    public ResponseEntity<Map<String, Object>> saveGoals(GoalsRequestDto goalsRequestDto, String jwtToken) {
        String username = helper.getUsername(jwtToken);
        try {
            if (!goalsRequestDto.isNewGoal()){
                Optional<Goal> goalById = goalRepository.findById(goalsRequestDto.getId());

                if (goalById.isPresent()){
                    return updateGoal(goalById.get(),goalsRequestDto,username);
                } else{
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Goal not found!"));
                }
            } else {
                Goal goal = Goal.builder()
                        .username(username)
                        .date(goalsRequestDto.getPlanDate())
                        .goalType(goalsRequestDto.getType())
                        .build();

                Goal savedGoal = goalRepository.save(goal);
                helper.createAndSaveGoalContainer(goalsRequestDto, savedGoal);
                log.info("571 savePlan method new plan=> {}", goal);

                saveGoalItem(goalsRequestDto, savedGoal);

                return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("id",savedGoal.getId()));
            }
        } catch (Exception e) {
            log.error("Error occurred while saving plan: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Error occurred while saving plan"));
        }
    }

    public ResponseEntity<Map<String, Object>> updateGoal(Goal goal, GoalsRequestDto goalRequestDto, String username) {
        log.info("updatePlan method PlanItemRequestDto=> {}", goalRequestDto);

        // GoalContainer'ları bir map'e çevirerek erişimi hızlandıralım
        Map<Integer, GoalContainer> goalContainerMap = goal.getGoalContainers().stream()
                .collect(Collectors.toMap(
                        GoalContainer::getIndex,
                        Function.identity(),
                        (existing, replacement) -> existing // Aynı index varsa, eski değeri koru
                ));

        for (PlanContainerRequestDto containerRequestDto : goalRequestDto.getGoalContainerRequestDtoList()) {
            GoalContainer goalContainer = goalContainerMap.get(containerRequestDto.getIndex());

            // Eğer GoalContainer yoksa, oluşturup ekleyelim
            if (goalContainer == null) {
                log.info("GoalContainer not found. Creating new... Index: {}, Name: {}",
                        containerRequestDto.getIndex(), containerRequestDto.getName());

                goalContainer = GoalContainer.builder()
                        .index(containerRequestDto.getIndex())
                        .name(containerRequestDto.getName())
                        .goal(goal)
                        .build();

                goalContainer = goalContainerRepository.save(goalContainer);
                goalContainerMap.put(goalContainer.getIndex(), goalContainer);
            }
            // Eğer mevcut container varsa, adı güncelleyelim
            else if (containerRequestDto.getName() != null
                    && !goalContainer.getName().equals(containerRequestDto.getName())) {
                goalContainer.setName(containerRequestDto.getName());
            }

            // PlanItem'ları güncelle
            helper.updateGoalPlanItem(goalContainer, containerRequestDto.getPlanItemList(), username);
        }
        
        helper.updateGoalItem(goal.getGoalItem(),goalRequestDto.getGoalItemRequestDto());

        log.info("savePlan method updated plan=> {}", goal);
        return ResponseEntity.ok().body(Map.of("id",goal.getId()));
    }

    private void saveGoalItem(GoalsRequestDto goalsRequestDto, Goal goal) {
        GoalItem goalItem = GoalItem.builder()
                .note(goalsRequestDto.getGoalItemRequestDto().getNote())
                .title(goalsRequestDto.getGoalItemRequestDto().getTitle())
                .goal(goal)
                .build();
        goalItemRepository.save(goalItem);
    }

    @Override
    public ResponseEntity<Goal> getGoalByUsernameAndDateAndType(String planDate, GoalType type, String jwtToken) {
        String username = helper.getUsername(jwtToken);
        log.info("{} {} get",username,planDate);

        LocalDate parsedDate = LocalDate.parse(planDate);

        Optional<Goal> goalByUsernameAndPlanDate = goalRepository.findGoalByUsernameAndPlanDateAndGoalType(username, parsedDate, type);
        if (goalByUsernameAndPlanDate.isPresent()){
            log.info("goal => {}",goalByUsernameAndPlanDate);
        }

        return goalByUsernameAndPlanDate.map(goal -> ResponseEntity.ok().body(goal)).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }

    @Override
    public ResponseEntity<List<GoalResponseDtoForConnect>> getGoalsByUsername(String jwtToken) {
        String username = helper.getUsername(jwtToken);
        log.info("{} get",username);

        Optional<List<Goal>> allGoals = goalRepository.findAllByUsername(username);
        if (allGoals.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        log.info("allGoals => {}",allGoals);

        return ResponseEntity.ok().body(allGoals.get().stream()
                .map(mapper::goalToGoalResponseDtoForConnect)
                .collect(Collectors.toList()));
    }

    @Override
    public ResponseEntity<String> updateGoalPlanStatusRequest(String jwtToken, PlanStatusUpdateRequestDto statusRequest) {
        log.info("updateGoalStatusRequest method is working ");
        KafkaRequest kafkaRequest = KafkaRequest.builder()
                .username(helper.getUsername(jwtToken))
                .goalId(statusRequest.getGoalId())
                .containerIndex(statusRequest.getContainerIndex())
                .planItemIndex(statusRequest.getPlanIndex())
                .status(statusRequest.getStatus())
                .build();

        log.info("kafkaRequest => {}",kafkaRequest.toString());
        kafkaTemplate.send("goals-status-topic",kafkaRequest);
        return ResponseEntity.ok().body("");
    }

    @Override
    @Transactional
    public void updateGoalPlanStatus(KafkaRequest kafkaRequest){
        Long goalId = kafkaRequest.getGoalId();

        log.info("updatePlanStatus listener triggered. GoalId => {}", goalId);

        Optional<Goal> goalOptional = goalRepository.findById(goalId); // <-- id ile buluyoruz

        goalOptional.ifPresentOrElse(goal -> {
            log.info("Goal found => {}", goal);

            List<GoalPlanItem> goalPlanItemList = goal.getGoalContainers().stream()
                    .filter(goalContainer -> goalContainer.getIndex() == kafkaRequest.getContainerIndex())
                    .findFirst()
                    .map(GoalContainer::getPlanItemList)
                    .orElse(Collections.emptyList());

            if (!goalPlanItemList.isEmpty()) {
                updateGoalPlanItemStatus(goalPlanItemList, kafkaRequest, goal.getUsername(), "Plan item status updated => {} ");
            } else {
                log.info("No matching GoalPlanItem found for update.");
            }
        }, () -> log.info("Goal not found by id: {}", goalId));
    }

    @Override
    @Transactional
    public ResponseEntity<String> deleteGoalPlanConnection(LocalDate planDate, String jwtToken) {
        String username = helper.getUsername(jwtToken);
        log.info("deleteGoalPlanConnection called. User: {}", username);

        Plan plan = planRepository.findPlanByUsernameAndPlanDate(username, planDate)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Plan not found!"));
        log.info("Plan found. Plan ID: {}, User: {}", plan.getId(), username);

        // Plan'ın Goal ile olan bağlantısını kaldır
        plan.setGoal(null);

        // PlanItem'ların GoalPlanItem ile olan bağlantılarını kes
        for (PlanItem planItem : plan.getPlanContainerList().get(0).getPlanItemList()) { //TODO
            planItem.setGoalPlanItem(null);
            planItemRepository.save(planItem);
        }

        planRepository.save(plan);
        log.info("Plan updated and saved. Plan ID: {} is no longer associated with any Goal.", plan.getId());

        return ResponseEntity.noContent().build(); // 204 No Content
    }

    @Override
    @Transactional
    public ResponseEntity<String> deleteGoal(String username, Long goalId) {
        Optional<Goal> goalById = goalRepository.findById(goalId);

        if (goalById.isPresent()){
            goalRepository.delete(goalById.get());
            return ResponseEntity.ok().body("Goal deleted");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Goal not found");
        }
    }

    private void updateGoalPlanItemStatus(List<GoalPlanItem> planItemList, KafkaRequest kafkaRequest, String username, String s) {
        for (GoalPlanItem item : planItemList) {
            if (item.getIndex() == kafkaRequest.getPlanItemIndex() &&
                    item.getUsername().equals(username)) {

                item.setStatus(kafkaRequest.getStatus());
                goalPlanItemRepository.save(item);

                log.info(s, item);
            }
        }
    }

    @Override
    public List<Goal> getActiveGoals(String username) {
        List<Goal> allGoals = goalRepository.findByUsername(username);
        LocalDate today = LocalDate.now();

        return allGoals.stream()
                .filter(goal -> isGoalActive(goal, today))
                .collect(Collectors.toList());
    }

    private boolean isGoalActive(Goal goal, LocalDate today) {
        LocalDate goalDate = goal.getDate();

        return switch (goal.getGoalType()) {
            case WEEKLY -> !today.isBefore(goalDate) && !today.isAfter(goalDate.plusDays(6));
            case MONTHLY -> today.getYear() == goalDate.getYear() &&
                    today.getMonth() == goalDate.getMonth();
            case YEARLY -> today.getYear() == goalDate.getYear();
        };
    }
}
