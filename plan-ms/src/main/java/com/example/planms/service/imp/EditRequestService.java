package com.example.planms.service.imp;

import com.example.planms.model.dto.request.*;
import com.example.planms.model.entity.Plan;
import com.example.planms.model.entity.PlanContainer;
import com.example.planms.model.entity.PlanItem;
import com.example.planms.repository.plan.PlanContainerRepository;
import com.example.planms.repository.plan.PlanItemRepository;
import com.example.planms.repository.plan.PlanRepository;
import com.example.planms.service.IEditRequestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class EditRequestService implements IEditRequestService {
    private final PlanRepository planRepository;
    private final PlanItemRepository planItemRepository;
    private final PlanContainerRepository planContainerRepository;

    public static final String STATUS_ACCEPT = "accept";

    /**
     * Edit Tipində gələn mesaj accept olduqda onun text hissəsini güncəlliyir
     * //TODO bunu kafkaya cala
     */
    @Override
    @Transactional
    public void updatePlanText(KafkaRequest requestDto) {
        log.info("updatePlanText method is working...");
        log.info("Listener planContainerList ==> {}",requestDto.getPlanContainerList());

        planRepository.findPlanByUsernameAndPlanDate(requestDto.getFirstUser(), requestDto.getPlanDate())
                .ifPresentOrElse(plan -> {
                    // PlanContainer'ları Map yapısına çevirerek erişimi hızlandır
                    Map<Integer, PlanContainer> existingContainers = plan.getPlanContainerList().stream()
                            .collect(Collectors.toMap(PlanContainer::getIndex, Function.identity()));

                    requestDto.getPlanContainerList().forEach(requestContainer -> {
                        PlanContainer existingContainer = existingContainers.get(requestContainer.getIndex());

                        if (existingContainer != null) {
                            log.info("{} index container found in plan", requestContainer.getIndex());
                            // Eğer PlanContainer varsa, PlanItem güncelleme ve ekleme işlemini yap
                            updateOrAddPlanItem(existingContainer, requestContainer);
                        } else {
                            log.warn("{} index container not found in plan", requestContainer.getIndex());
                            // Eğer PlanContainer yoksa, yeni olarak ekleyip kaydediyoruz
                            addPlanContainer(plan, requestContainer);
                        }
                    });

                    // Plan güncellendiği için kaydediyoruz
                    planRepository.save(plan);

                    log.info("Updated plan text for user: {} on date: {}", requestDto.getFirstUser(), requestDto.getPlanDate());
                    log.info("Updating plan text is successful!");
                }, () -> log.warn("Plan not found for user: {} on date: {}", requestDto.getFirstUser(), requestDto.getPlanDate()));
    }

    private void addPlanContainer(Plan plan, PlanContainer requestContainer) {
        PlanContainer newContainer = new PlanContainer();
        newContainer.setIndex(requestContainer.getIndex());
        newContainer.setName(requestContainer.getName());
        newContainer.setPlan(plan);

        // Yeni PlanItem'ları ekle
        List<PlanItem> newPlanItems = requestContainer.getPlanItemList().stream()
                .map(requestItem -> {
                    PlanItem newItem = new PlanItem();
                    newItem.setIndex(requestItem.getIndex());
                    newItem.setText(requestItem.getText());
                    newItem.setUsername(requestItem.getUsername());
                    newItem.setPlanContainer(newContainer);
                    return newItem;
                }).collect(Collectors.toList());

        newContainer.setPlanItemList(newPlanItems);

        // Yeni container'ı kaydet
        planContainerRepository.save(newContainer);
    }

    // PlanContainer içindeki PlanItem'ları güncelleyen metot
    private void updateOrAddPlanItem(PlanContainer existingContainer, PlanContainer requestContainer) {
        // PlanItem'ları Map yapısına çevirerek hızlı erişim sağla
        Map<Integer, PlanItem> existingItems = existingContainer.getPlanItemList().stream()
                .collect(Collectors.toMap(PlanItem::getIndex, Function.identity()));

        requestContainer.getPlanItemList().forEach(requestItem -> {
            int index = requestItem.getIndex();
            if (existingItems.containsKey(index)) {
                // Eğer PlanItem mevcutsa, text bilgisini güncelle
                existingItems.get(index).setText(requestItem.getText());
            } else {
                // Eğer PlanItem mevcut değilse, listeye ekle ve kaydet
                PlanItem newItem = new PlanItem();
                newItem.setIndex(requestItem.getIndex());
                newItem.setText(requestItem.getText());
                newItem.setPlanContainer(existingContainer);

                existingContainer.getPlanItemList().add(newItem);
                planItemRepository.save(newItem); // Yeni planItem kaydediliyor
            }
        });
    }

}

