package com.example.planms.kafka;

import com.example.planms.model.dto.request.KafkaRequest;
import com.example.planms.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Slf4j
@Service
public class KafkaListeners {
    private final IPlanService planService;
    private final IPlanItemService planItemService;
    private final IPlanContainerService planContainerService;

    private final IGoalService goalService;
    private final IGoalPlanItemService goalPlanItemService;
    private final IGoalContainerService goalContainerService;

    private final IUserService userService;
    private final IRankService rankService;
    private final IAnnounceService announceService;
    private final IEditRequestService editRequestService;
    private final ITimeNoteService timeNoteService;



    @KafkaListener(topics = "plan-status-topic",groupId = "groupId")
    void PlanStatusUpdatingListener(KafkaRequest request) {
        log.info("Listener is working 571");
        planService.updatePlanStatus(request);
    }

    @KafkaListener(topics = "goals-status-topic",groupId = "groupId")
    void GoalsStatusUpdatingListener(KafkaRequest request) {
        log.info("Goal Listener is working 571");
        goalService.updateGoalPlanStatus(request);
    }

    @KafkaListener(topics = "user-invite-topic",groupId = "groupId")
    void InviteUserListener(KafkaRequest request) {
        log.info("InviteUserListener is working 571");
        userService.inviteUser(request);
    }

    @KafkaListener(topics = "create-rank-topic",groupId = "groupId")
    void createRank(KafkaRequest request) {
        log.info("createRank is working 571");
        rankService.createRank(request.getUsername());
    }

    @KafkaListener(topics = "updateInvite-readingStatus-topic",groupId = "groupId")
    void updateInviteAnnounceReadingStatus(KafkaRequest request) {
        log.info("updateInviteAnnounceReadingStatus is working 571");
        announceService.updateInviteAnnounceReadingStatus(request.getIds());
    }

    @KafkaListener(topics = "create-sharedPlan-topic",groupId = "groupId")
    void createSharedPlan(KafkaRequest request) {
        log.info("createSharedPlan is working 571");
        announceService.createSharedPlan(request);
    }

//    @KafkaListener(topics = "sh-plan-status-topic",groupId = "groupId")
//    void SharedPlanStatusUpdatingListener(KafkaRequest request) {
//        log.info("update SharedPlan status is working 571");
//        sharedPlanService.updateSharedPlanStatus(request);
//    }
//
    @KafkaListener(topics = "accept-edit-message-topic",groupId = "groupId")
    void SharedPlanTextUpdatingListener(KafkaRequest request) {
        log.info("Kafka Listener => update SharedPlan text is working 571");
        editRequestService.updatePlanText(request);
    }

    @KafkaListener(topics = "planItem-connect-goalPlanItem",groupId = "groupId")
    void planItemConnectToGoalPlanItemListener(KafkaRequest request) {
        log.info("Kafka Listener => update planItemConnectToGoalPlanItemListener text is working 571");
        planService.connectPlanItemToGoalPlanItem(request);
    }

    @KafkaListener(topics = "planItem-disconnect-goalPlanItem",groupId = "groupId")
    void planItemDisconnectToGoalPlanItemListener(KafkaRequest request) {
        log.info("Kafka Listener => update planItemDisconnectToGoalPlanItemListener text is working 571");
        planService.disconnectPlanItemToGoalPlanItem(request);
    }

    @KafkaListener(topics = "timer-note-connect-topic",groupId = "groupId")
    void connectPlanContainerToTimeNoteListener(KafkaRequest request) {
        log.info("Kafka Listener => connectPlanContainerToTimeNoteListener listener is working 571");
        timeNoteService.connectToPlanContainer(request);
    }

    @KafkaListener(topics = "plan-item-text-topic",groupId = "groupId")
    void updatePlanItemTextListener(KafkaRequest request) {
        log.info("Kafka Listener => updatePlanItemTextListener listener is working 571");
        planItemService.savePlanItemKafkaConsumer(request);
    }

    @KafkaListener(topics = "plan-container-name-topic",groupId = "groupId")
    void updatePlanContainerNameListener(KafkaRequest request) {
        log.info("Kafka Listener => updatePlanContainerNameListener listener is working 571");
        planContainerService.updateContainerNameKafkaConsumer(request);
    }

    @KafkaListener(topics = "goal-item-text-topic",groupId = "groupId")
    void updateGoalPlanItemTextListener(KafkaRequest request) {
        log.info("Kafka Listener => updatePlanItemTextListener listener is working 571");
        goalPlanItemService.savePlanItemKafkaConsumer(request);
    }

    @KafkaListener(topics = "goal-container-name-topic",groupId = "groupId")
    void updateGoalContainerNameListener(KafkaRequest request) {
        log.info("Kafka Listener => updatePlanContainerNameListener listener is working 571");
        goalContainerService.updateContainerNameKafkaConsumer(request);
    }

    @KafkaListener(topics = "delete-plan-connection-topic",groupId = "groupId")
    void deletePlanTimeNoteConnectionListener(KafkaRequest request) {
        log.info("Kafka Listener => updatePlanContainerNameListener listener is working 571");
        planService.deletePlanTimeNoteConnection(request);
    }
}
