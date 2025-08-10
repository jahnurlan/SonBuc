package com.example.planms.service.imp;

import com.example.planms.model.dto.request.KafkaRequest;
import com.example.planms.model.entity.Plan;
import com.example.planms.model.entity.PlanContainer;
import com.example.planms.repository.plan.PlanContainerRepository;
import com.example.planms.repository.plan.PlanRepository;
import com.example.planms.service.ITimeNoteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class TimeNoteService implements ITimeNoteService {
    private final PlanRepository planRepository;
    private final PlanContainerRepository planContainerRepository;

    @Override
    @Transactional
    public void connectToPlanContainer(KafkaRequest request) {
        log.info("Kafka mesajı alındı: {}", request);
        log.info("connectToPlanContainer method is working..");

        String username = request.getUsername();
        LocalDate planDate = request.getPlanDate();
        int containerIndex = request.getContainerIndex();
        Long timeNoteId = request.getTimeNoteId();

        // 1. Mevcut konteyneri bul (index, username ve date ile)
        Optional<PlanContainer> optionalTargetContainer =
                planContainerRepository.findByUsernameAndDateAndIndex(username, planDate, containerIndex);

        PlanContainer targetContainer;

        if (optionalTargetContainer.isPresent()) {
            targetContainer = optionalTargetContainer.get();
            log.info("Existing PlanContainer found with id: {}", targetContainer.getId());
        } else {
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

            // 2. Eğer yoksa yeni bir konteyner oluştur
            targetContainer = PlanContainer.builder()
                    .username(username)
                    .planDate(planDate)
                    .index(containerIndex)
                    .plan(plan)
                    .build();
            planContainerRepository.save(targetContainer);
            log.info("New PlanContainer created with index {}", containerIndex);
        }

        // 3. Aynı güne ve kullanıcıya ait tüm konteynerleri getir
        List<PlanContainer> containersForDate = planContainerRepository.findAllByUsernameAndPlanDate(username, planDate);

        for (PlanContainer container : containersForDate) {
            // 4. Eğer başka bir konteyner aynı timeNoteId ile eşleşiyorsa resetle
            if (!container.getId().equals(targetContainer.getId())
                    && timeNoteId != null
                    && timeNoteId.equals(container.getTimeNoteId())) {
                container.setTimeNoteId(null);
                log.info("Reset timeNoteId in container id = {}", container.getId());
            }
        }

        // 5. Şimdi hedef konteynere timeNoteId bağla
        targetContainer.setTimeNoteId(timeNoteId);

        // 6. Değişiklikleri kaydet
        containersForDate.add(targetContainer); // Emin olmak için ekliyoruz
        planContainerRepository.saveAll(containersForDate);

        log.info("PlanContainer updated and timeNoteId connected: {}", targetContainer);
    }


}
