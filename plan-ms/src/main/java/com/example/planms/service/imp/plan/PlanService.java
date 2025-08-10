package com.example.planms.service.imp.plan;

import com.example.planms.mapper.ModelMapper;
import com.example.planms.model.dto.request.*;
import com.example.planms.model.dto.response.connect_goal.GoalResponseDtoForConnect;
import com.example.planms.model.entity.*;
import com.example.planms.repository.goal.GoalPlanItemRepository;
import com.example.planms.repository.goal.GoalRepository;
import com.example.planms.repository.plan.PlanContainerRepository;
import com.example.planms.repository.plan.PlanItemRepository;
import com.example.planms.repository.plan.PlanRepository;
import com.example.planms.service.IPlanService;
import com.example.planms.util.Helper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class PlanService implements IPlanService {
    private final PlanRepository planRepository;
    private final PlanContainerRepository planContainerRepository;
    private final PlanItemRepository planItemRepository;
    private final GoalRepository goalRepository;
    private final GoalPlanItemRepository goalPlanItemRepository;
    private final Helper helper;
    private final KafkaTemplate<String, KafkaRequest> kafkaTemplate;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public ResponseEntity<String> savePlan(PlanRequestDto planRequestDto, String jwtToken) {
        String username = helper.getUsername(jwtToken);

        try {
            Optional<Plan> optionalPlan = planRepository.findPlanByUsernameAndPlanDate(username, planRequestDto.getPlanDate());

            if (optionalPlan.isPresent()) {
                return updatePlan(optionalPlan.get(), planRequestDto, username);
            } else {
                Plan newPlan = Plan.builder()
                        .username(username)
                        .date(planRequestDto.getPlanDate())
                        .build();

                // Veritabanına kaydet → unique constraint varsa burada exception fırlayabilir
                Plan savedPlan = planRepository.save(newPlan);

                helper.createAndSavePlanContainer(planRequestDto, savedPlan);
                log.info("571 savePlan method new plan=> {}", savedPlan);

                return ResponseEntity.ok("Save is successful!");
            }

        } catch (DataIntegrityViolationException ex) {
            // Aynı kullanıcı ve tarihte plan varsa bu hata alınır
            log.warn("Duplicate plan detected, switching to update instead. User: {}, Date: {}", username, planRequestDto.getPlanDate());

            // Bu durumda tekrar sorgulayıp update yapılabilir
            Optional<Plan> existingPlan = planRepository.findPlanByUsernameAndPlanDate(username, planRequestDto.getPlanDate());
            if (existingPlan.isPresent()) {
                return updatePlan(existingPlan.get(), planRequestDto, username);
            }

            return ResponseEntity.status(HttpStatus.CONFLICT).body("Plan already exists, but couldn't be fetched for update.");

        } catch (Exception e) {
            log.error("Unexpected error during savePlan: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to save plan!");
        }
    }

    @Override
    public ResponseEntity<Plan> getPlanByUsernameAndDate(String date, String jwtToken) {
        String username = helper.getUsername(jwtToken);
        LocalDate dateTime = LocalDate.parse(date);
        log.info("{} {} get", username, dateTime);

        Optional<Plan> optionalPlan = planRepository.findPlanByUsernameAndPlanDate(username, dateTime);

        if (optionalPlan.isPresent()) {
            Plan plan = optionalPlan.get();
            log.info("Plan found => {}", plan);

            // If Plan is found, return it with HTTP status OK
            return ResponseEntity.ok().body(plan);
        } else {
            log.warn("No Plan or SharedPlan found for username: {} and date: {}", username, dateTime);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @Override
    @Transactional
    public ResponseEntity<String> updatePlan(Plan plan, PlanRequestDto planRequestDto, String username) {
        log.info("Updating Plan... Plan ID: {}", plan.getId());

        // PlanContainer'ları bir map'e çevirerek erişimi hızlandıralım
        Map<Integer, PlanContainer> planContainerMap = plan.getPlanContainerList().stream()
                .collect(Collectors.toMap(
                        PlanContainer::getIndex,
                        Function.identity(),
                        (existing, replacement) -> existing // Aynı index varsa, eski değeri koru
                ));

        for (PlanContainerRequestDto containerRequestDto : planRequestDto.getPlanContainerRequestDtoList()) {
            PlanContainer planContainer = planContainerMap.get(containerRequestDto.getIndex());

            // Eğer PlanContainer yoksa, oluşturup ekleyelim
            if (planContainer == null) {
                log.info("PlanContainer not found. Creating new... Index: {}, Name: {}",
                        containerRequestDto.getIndex(), containerRequestDto.getName());

                planContainer = PlanContainer.builder()
                        .index(containerRequestDto.getIndex())
                        .name(containerRequestDto.getName())
                        .plan(plan)
                        .build();

                planContainer = planContainerRepository.save(planContainer);
                planContainerMap.put(planContainer.getIndex(), planContainer);
            }
            // Eğer mevcut container varsa, adı güncelleyelim
            else if (containerRequestDto.getName() != null
                    && !planContainer.getName().equals(containerRequestDto.getName())) {
                planContainer.setName(containerRequestDto.getName());
            }

            // PlanItem'ları güncelle
            helper.updatePlanItem(planContainer, containerRequestDto.getPlanItemList(), username);
        }

        log.info("Plan successfully updated! Plan ID: {}", plan.getId());
        return ResponseEntity.ok("Update is successful!");
    }


    @Override
    public ResponseEntity<String> updatePlanStatusRequest(String jwtToken, PlanStatusUpdateRequestDto statusRequest) {
        log.info("updatePlanStatusRequest method triggered");
        String username = helper.getUsername(jwtToken);

        KafkaRequest kafkaRequest = KafkaRequest.builder()
                .username(username)
                .containerIndex(statusRequest.getContainerIndex())
                .planDate(statusRequest.getPlanDate())
                .planItemIndex(statusRequest.getPlanIndex())
                .status(statusRequest.getStatus())
                .build();

        log.info("kafkaRequest => {}", kafkaRequest.toString());
        kafkaTemplate.send("plan-status-topic", kafkaRequest);
        return ResponseEntity.ok().body("");
    }

    @Override
    @Transactional
    public void updatePlanStatus(KafkaRequest kafkaRequest) {
        log.info("Update Plan Status listener triggered for Username: {} and PlanDate: {}", kafkaRequest.getUsername(), kafkaRequest.getPlanDate());
        String username = kafkaRequest.getUsername();

        planRepository.findPlanByUsernameAndPlanDate(username, kafkaRequest.getPlanDate())
                .ifPresentOrElse(plan -> {
                    log.info("Plan found => {}", plan);

                    //TODO update plan status
                    List<PlanItem> planItemList = plan.getPlanContainerList().stream()
                            .filter(planContainer -> planContainer.getIndex() == kafkaRequest.getContainerIndex())
                            .findFirst()
                            .map(PlanContainer::getPlanItemList)
                            .orElse(Collections.emptyList());

                    if (planItemList.isEmpty()){
                        log.error("Plan Container planItemList is empty.That`s why you can`t give status. Container index => {}",kafkaRequest.getContainerIndex());
                    } else{
                        updatePlanItemStatus(planItemList, kafkaRequest, username,"Plan item status updated => {} ");
                    }
                }, () -> log.error("Plan not found for Username: {}", kafkaRequest.getUsername()));
    }

    private void updatePlanItemStatus(List<PlanItem> planItemList, KafkaRequest kafkaRequest, String username, String s) {
        for (PlanItem item : planItemList) {
            if (item.getIndex() == kafkaRequest.getPlanItemIndex() &&
                item.getUsername().equals(username)) {

                item.setStatus(kafkaRequest.getStatus());
                planItemRepository.save(item);

                log.info(s, item);
            }
        }
    }

    @Override
    @Transactional
    public ResponseEntity<String> connectPlanToGoal(PlanConnectRequestDto requestDto, String jwtToken){
        String username = helper.getUsername(jwtToken);

        Optional<Plan> optionalPlan = planRepository.findPlanByUsernameAndPlanDate(username,requestDto.getPlanDate());
        Optional<Goal> optionalGoal = goalRepository.findById(requestDto.getGoalId());

        if (optionalGoal.isPresent()){
            Goal goal = optionalGoal.get();
            log.info("Goal found => {}",goal);

            if (optionalPlan.isPresent()){
                Plan plan = optionalPlan.get();
                log.info("Plan found => {}",plan);

                plan.setGoal(goal);
                planRepository.save(plan);
                return ResponseEntity.ok().body(null);
            }else {
                Plan buildedPlan = Plan.builder()
                        .username(username)
                        .date(requestDto.getPlanDate())
                        .goal(goal)
                        .build();
                log.info("Plan not found and created => {}",buildedPlan);

                planRepository.save(buildedPlan);
                return ResponseEntity.status(HttpStatus.ACCEPTED).body("Plan Created and connected to goal");
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Goal not found!");
        }
    }

    @Override
    @Transactional
    public ResponseEntity<String> disconnectPlanToGoal(DisconnectGoalRequestDto requestDto, String username) {
        Plan plan = planRepository.findPlanByUsernameAndPlanDate(username,requestDto.getPlanDate())
                .orElseThrow(() -> new RuntimeException("Plan not found"));
        plan.setGoal(null);

        planRepository.save(plan);
        return ResponseEntity.ok().body("Plan disconnecting is successfully!");
    }

    @Override
    public ResponseEntity<GoalResponseDtoForConnect> getGoal(Long goalId, String jwtToken) {
        String username = helper.getUsername(jwtToken);

        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> {
                    log.error("Goal not found for id => {}",goalId);
                    return new EntityNotFoundException("Goal not found with ID: " + goalId);
                });

        if (!goal.getUsername().equals(username)){
            log.error("Username manipulation detected => {}",username);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        log.info("Everything is okay... GoalContainerList => {}",goal.getGoalContainers());
        return ResponseEntity.ok().body(modelMapper.goalToGoalResponseDtoForConnect(goal));
    }

    @Override
    public void connectPlanItemToGoalPlanItemKafkaRequest(PlanItemConnectRequestDto requestDto, String jwtToken){
        String username = helper.getUsername(jwtToken);
        KafkaRequest kafkaRequest = KafkaRequest.builder()
                .username(username)
                .planId(requestDto.getPlanId())
                .goalId(requestDto.getGoalId())
                .build();

        log.info("kafkaRequest => {}", kafkaRequest.toString());
        kafkaTemplate.send("planItem-connect-goalPlanItem", kafkaRequest);
    }

    @Override
    @Transactional
    public void connectPlanItemToGoalPlanItem(KafkaRequest request) {
        log.info("connectPlanItemToGoalPlanItem listener method is working...");

        GoalPlanItem goalPlanItem = goalPlanItemRepository.findById(request.getGoalId())
                .orElseThrow(() -> {
                    logError("Goal not found", request);
                    return new EntityNotFoundException("GoalPlanItem not found with ID: " + request.getGoalId());
                });

        if (!goalPlanItem.getUsername().equals(request.getUsername())) {
            logError("Username manipulation detected", request);
            return;
        }

        planItemRepository.findById(request.getPlanId())
                .ifPresentOrElse(
                        planItem -> {
                            planItem.setGoalPlanItem(goalPlanItem);
                            planItemRepository.save(planItem);
                        },
                        () -> logError("PlanItem not found", request)
                );
    }

    private void logError(String message, KafkaRequest request) {
        log.error("connectPlanItemToGoalPlanItem listener => {} -- GoalPlanItemId=> {} || Username=> {} || Process Time => {}",
                message, request.getGoalId(), request.getUsername(), LocalDateTime.now());
    }

    @Override
    public void disconnectPlanItemToGoalPlanItemKafkaRequest(PlanItemConnectRequestDto requestDto, String jwtToken) {
        String username = helper.getUsername(jwtToken);
        KafkaRequest kafkaRequest = KafkaRequest.builder()
                .username(username)
                .planId(requestDto.getPlanId())
                .goalId(requestDto.getGoalId())
                .build();

        log.info("kafkaRequest => {}", kafkaRequest.toString());
        kafkaTemplate.send("planItem-disconnect-goalPlanItem", kafkaRequest);
    }

    @Override
    @Transactional
    public void disconnectPlanItemToGoalPlanItem(KafkaRequest request) {
        log.info("connectPlanItemToGoalPlanItem listener method is working...");

        GoalPlanItem goalPlanItem = goalPlanItemRepository.findById(request.getGoalId())
                .orElseThrow(() -> {
                    logError("Goal not found", request);
                    return new EntityNotFoundException("GoalPlanItem not found with ID: " + request.getGoalId());
                });

        if (!goalPlanItem.getUsername().equals(request.getUsername())) {
            logError("Username manipulation detected", request);
            return;
        }

        planItemRepository.findById(request.getPlanId())
                .ifPresentOrElse(
                        planItem -> {
                            planItem.setGoalPlanItem(null);
                            planItemRepository.save(planItem);
                        },
                        () -> logError("PlanItem not found", request)
                );
    }

    @Override
    public List<Plan> getLast20DaysPlans(String jwtToken) {
        String username = helper.getUsername(jwtToken);
        LocalDate twentyDaysAgo = LocalDate.now().minusDays(20);

        return planRepository.findPlansFromLastDays(twentyDaysAgo, username);
    }

    @Override
    public ResponseEntity<?> updateNote(PlanNoteUpdateRequest request, String username) {
        LocalDate date = request.getDate();
        String note = request.getNote();

        Plan plan = planRepository.findPlanByUsernameAndPlanDate(username, date)
                .orElseThrow(() -> new IllegalArgumentException("Plan not found for date: " + date));

        plan.setNote(note);
        planRepository.save(plan);

        return ResponseEntity.ok().body(null);
    }

    @Override
    @Transactional
    public void deletePlanTimeNoteConnection(KafkaRequest request) {
        Plan plan = planRepository.findPlanByUsernameAndPlanDate(request.getUsername(), LocalDate.now())
                .orElseThrow(() -> new IllegalArgumentException("deletePlanTimeNoteConnection Plan not found for date: " + LocalDate.now()));

        List<PlanContainer> planContainerList = plan.getPlanContainerList();
        for (PlanContainer container : planContainerList) {
            if (container.getIndex() == request.getContainerIndex()) {
                container.setTimeNoteId(null);
                break;
            }
        }

        planRepository.save(plan);
    }
}

