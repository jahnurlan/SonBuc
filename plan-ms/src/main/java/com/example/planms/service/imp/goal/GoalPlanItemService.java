package com.example.planms.service.imp.goal;

import com.example.planms.model.dto.request.KafkaRequest;
import com.example.planms.model.dto.request.PlanItemRequestDto;
import com.example.planms.model.entity.*;
import com.example.planms.repository.goal.GoalContainerRepository;
import com.example.planms.repository.goal.GoalPlanItemRepository;
import com.example.planms.repository.goal.GoalRepository;
import com.example.planms.repository.plan.PlanContainerRepository;
import com.example.planms.repository.plan.PlanItemRepository;
import com.example.planms.repository.plan.PlanRepository;
import com.example.planms.service.IGoalPlanItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.OptimisticLockException;

@Service
@Slf4j
@RequiredArgsConstructor
public class GoalPlanItemService implements IGoalPlanItemService {
    private final GoalRepository goalRepository;
    private final GoalContainerRepository goalContainerRepository;
    private final GoalPlanItemRepository goalPlanItemRepository;
    private final KafkaTemplate<String, KafkaRequest> kafkaTemplate;

    @Override
    public ResponseEntity<String> savePlanItemKafkaProducer(PlanItemRequestDto requestDto, String username) {
        KafkaRequest kafkaRequest = KafkaRequest.builder()
                .username(username)
                .containerIndex(requestDto.getContainerIndex())
                .planItemIndex(requestDto.getIndex())
                .planItemText(requestDto.getText())
                .planDate(requestDto.getPlanDate())
                .goalType(requestDto.getGoalType())
                .build();

        log.info("savePlanItemKafkaProducer kafkaRequest => {}", kafkaRequest.toString());
        kafkaTemplate.send("goal-item-text-topic", kafkaRequest);
        return ResponseEntity.ok().body("");
    }

    @Override
    @Transactional
    public void savePlanItemKafkaConsumer(KafkaRequest kafkaRequest) {
        log.info("Kafka mesajı alındı: {}", kafkaRequest);

        try {
            // 1. PlanItem varsa doğrudan güncelle
            GoalPlanItem item = goalPlanItemRepository
                    .findByUsernameAndPlanDateAndContainerIndexAndIndex(kafkaRequest.getUsername(), kafkaRequest.getPlanDate(), kafkaRequest.getContainerIndex(), kafkaRequest.getPlanItemIndex())
                    .orElse(null);

            if (item != null) {
                log.info("PlanItem bulundu, güncelleniyor. ID: {}", item.getId());
                item.setText(kafkaRequest.getPlanItemText());

                try {
                    goalPlanItemRepository.save(item);
                    log.info("PlanItem güncellendi.");
                } catch (OptimisticLockException e) {
                    log.warn("OptimisticLock çakışması! PlanItem güncellenemedi. ID: {}", item.getId());
                }
                return;
            }

            // 2. Goal bulunup bulunmadığını kontrol et
            Goal goal = goalRepository
                    .findGoalByUsernameAndPlanDateAndGoalType(kafkaRequest.getUsername(), kafkaRequest.getPlanDate(),kafkaRequest.getGoalType())
                    .orElseGet(() -> {
                        Goal newGoal = Goal.builder()
                                .username(kafkaRequest.getUsername())
                                .date(kafkaRequest.getPlanDate())
                                .goalType(kafkaRequest.getGoalType())
                                .build();
                        Goal saved = goalRepository.save(newGoal);
                        log.info("Yeni Goal oluşturuldu. ID: {}", saved.getId());
                        return saved;
                    });

            // 3. PlanContainer'ı ara, yoksa oluştur
            GoalContainer container = goal.getGoalContainers().stream()
                    .filter(c -> c.getIndex() == kafkaRequest.getContainerIndex())
                    .findFirst()
                    .orElseGet(() -> {
                        GoalContainer newContainer = GoalContainer.builder()
                                .index(kafkaRequest.getContainerIndex())
                                .name("")
                                .goal(goal)
                                .date(kafkaRequest.getPlanDate())
                                .build();
                        GoalContainer saved = goalContainerRepository.save(newContainer);
                        log.info("Yeni GoalContainer oluşturuldu. ID: {}", saved.getId());
                        return saved;
                    });

            // 4. Yeni PlanItem oluştur
            GoalPlanItem newItem = GoalPlanItem.builder()
                    .index(kafkaRequest.getPlanItemIndex())
                    .text(kafkaRequest.getPlanItemText())
                    .username(kafkaRequest.getUsername())
                    .goalContainer(container)
                    .date(kafkaRequest.getPlanDate())
                    .build();

            goalPlanItemRepository.save(newItem);
            log.info("Yeni PlanItem oluşturuldu. ID: {}", newItem.getId());

        } catch (Exception e) {
            log.error("Kafka mesajı işlenirken hata oluştu: {}", e.getMessage(), e);
        }
    }
}
