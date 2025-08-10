package com.example.planms.service.imp.plan;

import com.example.planms.model.dto.request.KafkaContainerNameUpdateRequest;
import com.example.planms.model.dto.request.KafkaRequest;
import com.example.planms.model.entity.Plan;
import com.example.planms.model.entity.PlanContainer;
import com.example.planms.repository.plan.PlanContainerRepository;
import com.example.planms.repository.plan.PlanRepository;
import com.example.planms.service.IPlanContainerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class PlanContainerService implements IPlanContainerService {
    private final PlanRepository planRepository;
    private final PlanContainerRepository planContainerRepository;
    private final KafkaTemplate<String, KafkaRequest> kafkaTemplate;

    @Override
    public ResponseEntity<String> updateContainerNameKafkaProducer(KafkaContainerNameUpdateRequest request, String username) {
        KafkaRequest kafkaRequest = KafkaRequest.builder()
                .username(username)
                .planDate(request.getPlanDate())
                .containerIndex(request.getContainerIndex())
                .containerName(request.getNewName())
                .build();

        log.info("PlanContainer name update Kafka producer: {}", kafkaRequest);
        kafkaTemplate.send("plan-container-name-topic", kafkaRequest);
        return ResponseEntity.ok("Container name update mesajı gönderildi.");
    }

    @Override
    @Transactional
    public void updateContainerNameKafkaConsumer(KafkaRequest request) {
        log.info("Kafka mesajı alındı: PlanContainer name güncellemesi: {}", request);

        try {
            // 1. PlanContainer'ı doğrudan unique kombinasyonla ara
            Optional<PlanContainer> optionalContainer = planContainerRepository
                    .findByUsernameAndDateAndIndex(request.getUsername(), request.getPlanDate(), request.getContainerIndex());

            if (optionalContainer.isPresent()) {
                PlanContainer container = optionalContainer.get();

                if (!Objects.equals(container.getName(), request.getContainerName())) {
                    container.setName(request.getContainerName());
                    planContainerRepository.save(container);
                    log.info("PlanContainer ismi güncellendi. ID: {}, Yeni isim: {}", container.getId(), container.getName());
                } else {
                    log.info("Aynı isim gönderildi. Güncelleme yapılmadı.");
                }


                return;
            }

            // 2. Plan yoksa oluştur
            Plan plan = planRepository
                    .findPlanByUsernameAndPlanDate(request.getUsername(), request.getPlanDate())
                    .orElseGet(() -> {
                        Plan newPlan = Plan.builder()
                                .username(request.getUsername())
                                .date(request.getPlanDate())
                                .build();
                        Plan saved = planRepository.save(newPlan);
                        log.info("Plan bulunamadı. Yeni Plan oluşturuldu. ID: {}", saved.getId());
                        return saved;
                    });

            // 3. PlanContainer oluştur ve ismini ata
            PlanContainer newContainer = PlanContainer.builder()
                    .username(request.getUsername())
                    .index(request.getContainerIndex())
                    .name(request.getContainerName())
                    .plan(plan)
                    .planDate(request.getPlanDate())
                    .build();

            planContainerRepository.save(newContainer);
            log.info("Yeni PlanContainer oluşturuldu. Index: {}, Name: {}", newContainer.getIndex(), newContainer.getName());

        } catch (Exception e) {
            log.error("PlanContainer name güncellenirken hata: {}", e.getMessage(), e);
        }
    }

}
