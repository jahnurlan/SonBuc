package com.example.planms.util;

import com.example.commonsecurity.auth.services.JwtService;
import com.example.planms.model.dto.request.*;
import com.example.planms.model.entity.*;
import com.example.planms.repository.*;
import com.example.planms.repository.goal.GoalContainerRepository;
import com.example.planms.repository.goal.GoalItemRepository;
import com.example.planms.repository.goal.GoalPlanItemRepository;
import com.example.planms.repository.plan.PlanContainerRepository;
import com.example.planms.repository.plan.PlanItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
@Configuration
public class Helper {
    private final JwtService jwtService;
    private final PlanContainerRepository planContainerRepository;
    private final PlanItemRepository planItemRepository;
    private final GoalContainerRepository goalContainerRepository;
    private final GoalPlanItemRepository goalPlanItemRepository;
    private final GoalItemRepository goalItemRepository;
    private final RankRepository rankRepository;
    private static final Map<String, Integer> POINTS_MAP = new HashMap<>();

    static {
        POINTS_MAP.put("Finished", 20);
        POINTS_MAP.put("Almost Done", 10);
        POINTS_MAP.put("important", 15);
        POINTS_MAP.put("required", 10);
        POINTS_MAP.put("nice", 5);
    }

    public String getUsername(String jwtToken) {
        String jwt = jwtToken.substring(7);
        return jwtService.extractUsername(jwt);
    }

    public void updatePlanItem(PlanContainer planContainer, List<PlanItemRequestDto> planItemRequestDtoList, String username) {
        log.info("Updating PlanItems for Container Index: {}", planContainer.getIndex());

        // Eğer planItemList null ise, boş bir listeyle değiştir
        List<PlanItem> existingPlanItems = planContainer.getPlanItemList();
        if (existingPlanItems == null) {
            existingPlanItems = new ArrayList<>();
        }

        // Mevcut PlanItem'ları bir map'e çevirerek erişimi hızlandıralım
        Map<Integer, PlanItem> planItemMap = existingPlanItems.stream()
                .collect(Collectors.toMap(PlanItem::getIndex, Function.identity()));

        for (PlanItemRequestDto dto : planItemRequestDtoList) {
            PlanItem planItem = planItemMap.get(dto.getIndex());

            if (planItem != null) {
                // Eğer PlanItem zaten varsa, sadece text'i güncelleyelim
                if (!Objects.equals(planItem.getText(), dto.getText())) {
                    planItem.setText(dto.getText());
                    if (dto.getStatus() != null) {
                        planItem.setStatus(dto.getStatus());
                    }
                }
            } else {
                // Yeni PlanItem oluştur
                planItem = PlanItem.builder()
                        .planContainer(planContainer)
                        .username(username)
                        .index(dto.getIndex())
                        .text(dto.getText())
                        .status(dto.getStatus())
                        .build();
                planItemRepository.save(planItem);
            }
        }
    }


    public void updateGoalPlanItem(GoalContainer goalContainer, List<PlanItemRequestDto> planItemRequestDtoList, String username) {
        log.info("Update GoalPlanItem method working... PlanItemList from db => {} \n PlanItemList from request => {} \n username from token => {} \n GoalContainer => {}", goalContainer.getPlanItemList(), planItemRequestDtoList, username, goalContainer);

        // Eğer planItemList null ise, boş bir listeyle değiştir
        List<GoalPlanItem> existingGoalPlanItems = goalContainer.getPlanItemList();
        if (existingGoalPlanItems == null) {
            existingGoalPlanItems = new ArrayList<>();
        }

        // Mevcut GoalPlanItem'ları bir map'e çevirerek erişimi hızlandıralım
        Map<Integer, GoalPlanItem> goalPlanItemMap = existingGoalPlanItems.stream()
                .collect(Collectors.toMap(GoalPlanItem::getIndex, Function.identity()));

        for (PlanItemRequestDto dto : planItemRequestDtoList) {
            GoalPlanItem planItem = goalPlanItemMap.get(dto.getIndex());

            if (planItem != null) {
                // Eğer PlanItem zaten varsa, sadece text'i güncelleyelim
                if (!Objects.equals(planItem.getText(), dto.getText())) {
                    planItem.setText(dto.getText());
                    if (dto.getStatus() != null) {
                        planItem.setStatus(dto.getStatus());
                    }
                }
            } else {
                // Yeni PlanItem oluştur
                planItem = GoalPlanItem.builder()
                        .goalContainer(goalContainer)
                        .username(username)
                        .index(dto.getIndex())
                        .text(dto.getText())
                        .status(dto.getStatus())
                        .build();
                goalPlanItemRepository.save(planItem);
            }
        }
    }

    public void updateGoalItem(GoalItem goalItem, GoalItemRequestDto requestDto) {
        goalItem.setNote(requestDto.getNote().trim());
        goalItem.setTitle(requestDto.getTitle().trim());
        goalItemRepository.save(goalItem);
    }

    @Transactional
    public void createAndSavePlanContainer(PlanRequestDto planRequestDto, Plan plan) {
        log.info("Creating PlanContainers for Plan: {}", plan.getId());

        List<PlanContainer> planContainers = new ArrayList<>();

        for (PlanContainerRequestDto planContainerRequest : planRequestDto.getPlanContainerRequestDtoList()) {
            PlanContainer planContainer = PlanContainer.builder()
                    .name(planContainerRequest.getName())
                    .index(planContainerRequest.getIndex())
                    .plan(plan)
                    .build();
            planContainers.add(planContainer);
        }

        // Önce planContainer'ları kaydet ve veritabanından ID al
        planContainerRepository.saveAll(planContainers);

        List<PlanItem> planItems = new ArrayList<>();

        for (PlanContainer planContainer : planContainers) {
            // İlgili request DTO'yu bul
            PlanContainerRequestDto planContainerRequest = planRequestDto.getPlanContainerRequestDtoList().stream()
                    .filter(req -> req.getIndex() == planContainer.getIndex())
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Matching PlanContainerRequestDto not found"));

            for (PlanItemRequestDto item : planContainerRequest.getPlanItemList()) {
                PlanItem planItem = PlanItem.builder()
                        .username(plan.getUsername())
                        .index(item.getIndex())
                        .text(item.getText())
                        .status(item.getStatus())
                        .planContainer(planContainer) // Artık planContainer'ın ID'si var
                        .build();
                planItems.add(planItem);
            }
        }

        // Şimdi planItem'ları kaydet
        planItemRepository.saveAll(planItems);

        log.info("Successfully created {} PlanContainers and {} PlanItems.", planContainers.size(), planItems.size());
    }

    @Transactional
    public void createAndSaveGoalContainer(GoalsRequestDto goalsRequestDto, Goal goal) {
        log.info("Creating GoalContainers for Goal: {}", goal.getId());

        List<GoalContainer> goalContainers = new ArrayList<>();

        for (PlanContainerRequestDto planContainerRequest : goalsRequestDto.getGoalContainerRequestDtoList()) {
            GoalContainer goalContainer = GoalContainer.builder()
                    .username(goal.getUsername())
                    .name(planContainerRequest.getName())
                    .index(planContainerRequest.getIndex())
                    .date(goal.getDate())
                    .goal(goal)
                    .build();
            goalContainers.add(goalContainer);
        }

        // Önce goalContainer'ları kaydet ve veritabanından ID al
        goalContainerRepository.saveAll(goalContainers);

        List<GoalPlanItem> goalPlanItems = new ArrayList<>();

        for (GoalContainer goalContainer : goalContainers) {
            // İlgili request DTO'yu bul
            PlanContainerRequestDto planContainerRequest = goalsRequestDto.getGoalContainerRequestDtoList().stream()
                    .filter(req -> req.getIndex() == goalContainer.getIndex())
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Matching GoalContainerRequestDto not found"));

            for (PlanItemRequestDto item : planContainerRequest.getPlanItemList()) {
                GoalPlanItem goalPlanItem = GoalPlanItem.builder()
                        .username(goal.getUsername())
                        .date(goal.getDate())
                        .index(item.getIndex())
                        .text(item.getText())
                        .status(item.getStatus())
                        .goalContainer(goalContainer) // Artık goalContainer'ın ID'si var
                        .build();
                goalPlanItems.add(goalPlanItem);
            }
        }

        // Şimdi planItem'ları kaydet
        goalPlanItemRepository.saveAll(goalPlanItems);

        log.info("Successfully created {} PlanContainers and {} PlanItems.", goalContainers.size(), goalPlanItems.size());

    }

    public void increasePoint(String username, int point) {
        rankRepository.findByUsername(username).ifPresentOrElse(rank -> {
            rank.setPoint(rank.getPoint() + point);
            rankRepository.save(rank);
            log.info("Rank point updated => {}", rank);
        }, () -> {
            Rank newRank = Rank.builder()
                    .point(point)
                    .username(username)
                    .build();
            rankRepository.save(newRank);
            log.info("New rank created and saved => {}", newRank);
        });
    }

    public void updatePointByType(String username, String type) {
        Optional.ofNullable(POINTS_MAP.get(type)).ifPresent(points -> increasePoint(username, points));
    }

    public void updatePointByGoalType(String username, String type, int typePoint) {
        Optional.ofNullable(POINTS_MAP.get(type)).ifPresent(points -> increasePoint(username, points + typePoint));
    }

    private void updatePointsForTexts(String username, String type, String... texts) {
        for (String text : texts) {
            if (!text.isEmpty()) {
                updatePointByType(username, type);
            }
        }
    }

//    public void updatePoint(PlanItemRequestDtoddd requestDto, String username) {
//        updatePointsForTexts(username, "important", requestDto.getImp1Text().trim(), requestDto.getImp2Text().trim(), requestDto.getImp3Text().trim());
//        updatePointsForTexts(username, "required", requestDto.getReq1Text().trim(), requestDto.getReq2Text().trim(), requestDto.getReq3Text().trim());
//        updatePointsForTexts(username, "nice", requestDto.getNice1Text().trim(), requestDto.getNice2Text().trim(), requestDto.getNice3Text().trim());
//    }
//
//    public void updatePointByExistingPlan(PlanItemkk planItemkk, PlanItemRequestDtoddd requestDto, String username) {
//        resetTextsForNonEmptyFields(planItemkk, requestDto);
//        updatePoint(requestDto, username);
//    }
//
//    public void updatePointByExistingGoal(PlanItemkk planItemkk, PlanItemRequestDtoddd requestDto, String username, String goalType) {
//        if (planItemkk == null || requestDto == null || username == null || goalType == null) {
//            throw new IllegalArgumentException("PlanItemkk, RequestDto, username or goalType cannot be null");
//        }
//
//        resetTextsForNonEmptyFields(planItemkk, requestDto);
//        updatePointForGoal(requestDto, username, goalType);
//    }
//
//    public void updatePointForGoal(PlanItemRequestDtoddd requestDto, String username, String goalType) {
//        int typePoint = switch (goalType) {
//            case "Yearly" -> 80;
//            case "Monthly" -> 30;
//            default -> 10;
//        };
//        updatePointsForGoalTexts(username, "important", typePoint, requestDto.getImp1Text().trim(), requestDto.getImp2Text().trim(), requestDto.getImp3Text().trim());
//        updatePointsForGoalTexts(username, "required", typePoint, requestDto.getReq1Text().trim(), requestDto.getReq2Text().trim(), requestDto.getReq3Text().trim());
//        updatePointsForGoalTexts(username, "nice", typePoint, requestDto.getNice1Text().trim(), requestDto.getNice2Text().trim(), requestDto.getNice3Text().trim());
//    }
//
//    private void updatePointsForGoalTexts(String username, String type,int typePoint, String... texts) {
//        for (String text : texts) {
//            if (!text.isEmpty()) {
//                updatePointByGoalType(username, type , typePoint);
//            }
//        }
//    }

    private void resetField(Supplier<String> getText, Supplier<Boolean> isPointActive, Consumer<Boolean> setPointActive, Consumer<String> setText) {
        if (!getText.get().isEmpty() || isPointActive.get()) {
            setText.accept("");
            setPointActive.accept(true);
        }
    }

    public String findLevel(int point) {
        if (point < 100) {
            return "Beginner";
        } else if (point < 200) {
            return "Intermediate";
        } else {
            return "Hero";
        }

    }
}
