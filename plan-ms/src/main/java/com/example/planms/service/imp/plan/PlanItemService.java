package com.example.planms.service.imp.plan;

import com.example.planms.model.dto.request.*;
import com.example.planms.model.entity.Plan;
import com.example.planms.model.entity.PlanContainer;
import com.example.planms.model.entity.PlanItem;
import com.example.planms.repository.plan.PlanContainerRepository;
import com.example.planms.repository.plan.PlanItemRepository;
import com.example.planms.repository.plan.PlanRepository;
import com.example.planms.service.IPlanItemService;
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
public class PlanItemService implements IPlanItemService {
    private final PlanRepository planRepository;
    private final PlanContainerRepository planContainerRepository;
    private final PlanItemRepository planItemRepository;
    private final KafkaTemplate<String, KafkaRequest> kafkaTemplate;

    @Override
    public ResponseEntity<String> savePlanItemKafkaProducer(PlanItemRequestDto requestDto, String username) {
        KafkaRequest kafkaRequest = KafkaRequest.builder()
                .username(username)
                .containerIndex(requestDto.getContainerIndex())
                .planItemIndex(requestDto.getIndex())
                .planItemText(requestDto.getText())
                .planDate(requestDto.getPlanDate())
                .build();

        log.info("savePlanItemKafkaProducer kafkaRequest => {}", kafkaRequest.toString());
        kafkaTemplate.send("plan-item-text-topic", kafkaRequest);
        return ResponseEntity.ok().body("");
    }

    @Override
    @Transactional
    public void savePlanItemKafkaConsumer(KafkaRequest kafkaRequest) {
        log.info("Kafka mesajı alındı: {}", kafkaRequest);

        try {
            // 1. PlanItem varsa doğrudan güncelle
            PlanItem item = planItemRepository
                    .findByUsernameAndPlanDateAndContainerIndexAndIndex(kafkaRequest.getUsername(), kafkaRequest.getPlanDate(), kafkaRequest.getContainerIndex(), kafkaRequest.getPlanItemIndex())
                    .orElse(null);

            if (item != null) {
                log.info("PlanItem bulundu, güncelleniyor. ID: {}", item.getId());
                item.setText(kafkaRequest.getPlanItemText());

                try {
                    planItemRepository.save(item);
                    log.info("PlanItem güncellendi.");
                } catch (OptimisticLockException e) {
                    log.warn("OptimisticLock çakışması! PlanItem güncellenemedi. ID: {}", item.getId());
                }
                return;
            }

            // 2. Plan bulunup bulunmadığını kontrol et
            Plan plan = planRepository
                    .findPlanByUsernameAndPlanDate(kafkaRequest.getUsername(), kafkaRequest.getPlanDate())
                    .orElseGet(() -> {
                        Plan newPlan = Plan.builder()
                                .username(kafkaRequest.getUsername())
                                .date(kafkaRequest.getPlanDate())
                                .build();
                        Plan saved = planRepository.save(newPlan);
                        log.info("Yeni Plan oluşturuldu. ID: {}", saved.getId());
                        return saved;
                    });

            // 3. PlanContainer'ı ara, yoksa oluştur
            PlanContainer container = plan.getPlanContainerList().stream()
                    .filter(c -> c.getIndex() == kafkaRequest.getContainerIndex())
                    .findFirst()
                    .orElseGet(() -> {
                        PlanContainer newContainer = PlanContainer.builder()
                                .username(kafkaRequest.getUsername())
                                .index(kafkaRequest.getContainerIndex())
                                .name("")
                                .plan(plan)
                                .planDate(kafkaRequest.getPlanDate())
                                .build();
                        PlanContainer saved = planContainerRepository.save(newContainer);
                        log.info("Yeni PlanContainer oluşturuldu. ID: {}", saved.getId());
                        return saved;
                    });

            // 4. Yeni PlanItem oluştur
            PlanItem newItem = PlanItem.builder()
                    .index(kafkaRequest.getPlanItemIndex())
                    .text(kafkaRequest.getPlanItemText())
                    .username(kafkaRequest.getUsername())
                    .planContainer(container)
                    .planDate(kafkaRequest.getPlanDate())
                    .build();

            planItemRepository.save(newItem);
            log.info("Yeni PlanItem oluşturuldu. ID: {}", newItem.getId());

        } catch (Exception e) {
            log.error("Kafka mesajı işlenirken hata oluştu: {}", e.getMessage(), e);
        }
    }
}
