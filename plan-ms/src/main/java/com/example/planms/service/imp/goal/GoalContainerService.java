package com.example.planms.service.imp.goal;

import com.example.planms.model.dto.request.KafkaContainerNameUpdateRequest;
import com.example.planms.model.dto.request.KafkaRequest;
import com.example.planms.model.entity.*;
import com.example.planms.repository.goal.GoalContainerRepository;
import com.example.planms.repository.goal.GoalRepository;
import com.example.planms.service.IGoalContainerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class GoalContainerService implements IGoalContainerService {
    private final GoalRepository goalRepository;
    private final GoalContainerRepository goalContainerRepository;
    private final KafkaTemplate<String, KafkaRequest> kafkaTemplate;

    @Override
    public ResponseEntity<String> updateContainerNameKafkaProducer(KafkaContainerNameUpdateRequest request, String username) {
        KafkaRequest kafkaRequest = KafkaRequest.builder()
                .username(username)
                .planDate(request.getPlanDate())
                .containerIndex(request.getContainerIndex())
                .containerName(request.getNewName())
                .goalType(request.getGoalType())
                .build();

        log.info("PlanContainer name update Kafka producer: {}", kafkaRequest);
        kafkaTemplate.send("goal-container-name-topic", kafkaRequest);
        return ResponseEntity.ok("Container name update mesajı gönderildi.");
    }

    @Override
    @Transactional
    public void updateContainerNameKafkaConsumer(KafkaRequest request) {
        log.info("Kafka mesajı alındı: GoalContainer name güncellemesi: {}", request);

        try {
            // 1. PlanContainer'ı doğrudan unique kombinasyonla ara
            Optional<GoalContainer> optionalContainer = goalContainerRepository
                    .findByUsernameAndDateAndIndex(request.getUsername(), request.getPlanDate(), request.getContainerIndex());

            if (optionalContainer.isPresent()) {
                GoalContainer container = optionalContainer.get();

                if (!container.getName().equals(request.getContainerName())) {
                    container.setName(request.getContainerName());
                    goalContainerRepository.save(container);
                    log.info("GoalContainer ismi güncellendi. ID: {}, Yeni isim: {}", container.getId(), container.getName());
                } else {
                    log.info("Aynı isim gönderildi. Güncelleme yapılmadı.");
                }

                return;
            }

            // 2. Plan yoksa oluştur
            Goal goal = goalRepository
                    .findGoalByUsernameAndPlanDateAndGoalType(request.getUsername(), request.getPlanDate(), request.getGoalType())
                    .orElseGet(() -> {
                        Goal newGoal = Goal.builder()
                                .username(request.getUsername())
                                .date(request.getPlanDate())
                                .goalType(request.getGoalType())
                                .build();
                        Goal saved = goalRepository.save(newGoal);
                        log.info("Goal bulunamadı. Yeni Goal oluşturuldu. ID: {}", saved.getId());
                        return saved;
                    });

            // 3. PlanContainer oluştur ve ismini ata
            GoalContainer newContainer = GoalContainer.builder()
                    .index(request.getContainerIndex())
                    .name(request.getContainerName())
                    .goal(goal)
                    .date(request.getPlanDate())
                    .build();

            goalContainerRepository.save(newContainer);
            log.info("Yeni GoalContainer oluşturuldu. Index: {}, Name: {}", newContainer.getIndex(), newContainer.getName());

        } catch (Exception e) {
            log.error("GoalContainer name güncellenirken hata: {}", e.getMessage(), e);
        }
    }

}
