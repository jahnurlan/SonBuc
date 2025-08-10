package com.example.planms.service.imp;

import com.example.planms.model.dto.request.InviteStatusRequestDto;
import com.example.planms.model.dto.request.KafkaRequest;
import com.example.planms.model.dto.response.InviteAnnounceResponseDto;
import com.example.planms.model.entity.InviteAnnounce;
import com.example.planms.model.entity.Plan;
import com.example.planms.model.enums.PlanType;
import com.example.planms.repository.InviteAnnounceRepository;
import com.example.planms.repository.plan.PlanItemRepository;
import com.example.planms.repository.plan.PlanRepository;
import com.example.planms.service.IAnnounceService;
import com.example.planms.util.Helper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class AnnounceService implements IAnnounceService {
    private final KafkaTemplate<String, KafkaRequest> kafkaTemplate;
    private final InviteAnnounceRepository inviteAnnounceRepository;
    private final Helper helper;
    private final PlanItemRepository planItemRepository;
    private final PlanRepository planRepository;

    @Override
    public ResponseEntity<Long> getInviteAnnounceCount(String jwtToken) {
        String username = helper.getUsername(jwtToken);

        long inviteCount = inviteAnnounceRepository.countByUsernameAndStatusTrue(username);

        // Sonucu ResponseEntity içinde döndür
        return new ResponseEntity<>(inviteCount, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<InviteAnnounceResponseDto>> getAllInviteAnnounce(String jwtToken) {
        String username = helper.getUsername(jwtToken);
        List<InviteAnnounce> allActiveAnnounce = inviteAnnounceRepository.getAllActiveAnnounce(username);

        log.info("All invited request => {}",allActiveAnnounce);
        // Manuel Mapping ve Stream API Using
        LocalDateTime now = LocalDateTime.now();
        List<InviteAnnounceResponseDto> inviteAnnounceResponseDtos = allActiveAnnounce.stream()
                .map(inviteAnnounce -> new InviteAnnounceResponseDto(
                        inviteAnnounce.getId(),
                        inviteAnnounce.getPlanDate(),
                        inviteAnnounce.getUsername(),
                        calculateTimeAgo(inviteAnnounce.getAnnounceDate(), now),
                        inviteAnnounce.isReadingStatus()
                ))
                .collect(Collectors.toList());

        updateReadingStatusRequest(allActiveAnnounce);

        return ResponseEntity.ok().body(inviteAnnounceResponseDtos);
    }

    private String calculateTimeAgo(LocalDateTime announceDate, LocalDateTime now) {
        Duration duration = Duration.between(announceDate,now);

        long days = duration.toDays();
        long hours = duration.toHours();
        long minutes = duration.toMinutes();
        long seconds = duration.getSeconds();

        if (days > 0) {
            return days + " days ago";
        } else if (hours > 0) {
            return hours + " hours ago";
        } else if (minutes > 0) {
            return minutes + " minutes ago";
        } else {
            return seconds + " seconds ago";
        }
    }

    private void updateReadingStatusRequest(List<InviteAnnounce> allActiveAnnounce) {
        List<Long> ids = allActiveAnnounce.stream()
                .map(InviteAnnounce::getId)
                .toList();

        KafkaRequest kafkaRequest = KafkaRequest.builder()
                .ids(ids)
                .build();
        log.info("kafkaRequest => {}",kafkaRequest.toString());

        kafkaTemplate.send("updateInvite-readingStatus-topic",kafkaRequest);
    }

    @Transactional
    public void updateInviteAnnounceReadingStatus(List<Long> ids){
        int updatedRowsCount = inviteAnnounceRepository.updateReadingStatusByIdsInBatch(ids);
        log.info("{} rows Reading Status updated!",updatedRowsCount);
    }

    @Override
    @Transactional
    public void rejectInviteRequest(InviteStatusRequestDto inviteStatusRequestDto, String jwtToken) {
        String username = helper.getUsername(jwtToken);
        Optional<InviteAnnounce> announceByUsernameAndPlanDate = inviteAnnounceRepository.findAnnounceByUsernameAndId(inviteStatusRequestDto.getFirstUsername(), username, inviteStatusRequestDto.getId());

        log.info("rejectInviteRequest is working....");

        if (announceByUsernameAndPlanDate.isPresent()){
            InviteAnnounce inviteAnnounce = announceByUsernameAndPlanDate.get();
            inviteAnnounce.setStatus(false);
            inviteAnnounceRepository.save(inviteAnnounce);

            log.info("inviteAnnounce updated => {}",inviteAnnounce);
        } else {
            log.error("announceByUsernameAndPlanDate not founded for => {}.inviteStatusRequestDto => {}",username,inviteStatusRequestDto);
        }
    }

    @Override
    @Transactional
    public void acceptInviteRequest(InviteStatusRequestDto inviteStatusRequestDto, String jwtToken) {
        String tokenUsername = helper.getUsername(jwtToken);
        Optional<InviteAnnounce> announceByUsernameAndPlanDate = inviteAnnounceRepository.findAnnounceByUsernameAndId(inviteStatusRequestDto.getFirstUsername(), tokenUsername, inviteStatusRequestDto.getId());

        log.info("acceptInviteRequest is working....");

        if (announceByUsernameAndPlanDate.isPresent()){
            createSharedPlanRequest(tokenUsername,inviteStatusRequestDto,announceByUsernameAndPlanDate.get().getPlanDate());

            InviteAnnounce inviteAnnounce = announceByUsernameAndPlanDate.get();
            inviteAnnounce.setStatus(false);
            inviteAnnounceRepository.save(inviteAnnounce);

            log.info("inviteAnnounce updated => {}",inviteAnnounce);
        } else {
            log.error("announceByUsernameAndPlanDate not founded for => {}.inviteStatusRequestDto => {}",tokenUsername,inviteStatusRequestDto);
        }
    }

    @Override
    @Transactional
    public void createSharedPlan(KafkaRequest request) {
        log.info("📌 Shared Plan oluşturuluyor. Owner: {}, Invited: {}, Tarih: {}",
                request.getFirstUsername(), request.getUsername(), request.getPlanDateTime());

        Plan inviteOwnerPlan = getOrCreatePlan(request.getFirstUsername(), request.getPlanDateTime());
        Plan acceptPersonPlan = getOrCreatePlan(request.getUsername(), request.getPlanDateTime());

        // **Gruba yeni eklenen kişiyi, tüm diğer bağlantılara ekliyoruz.**
        log.info("🔗 {}’ın bağlı olduğu tüm planlara {} ekleniyor...", request.getFirstUsername(), request.getUsername());
        for (Plan existingPlan : inviteOwnerPlan.getConnectedPlans()) {
            existingPlan.connectToGroup(acceptPersonPlan);
            planRepository.save(existingPlan);
        }

        // **Nurlan ile Orhan’ı birbirine bağlıyoruz**
        log.info("🔗 {} ve {} birbirine bağlanıyor...", request.getFirstUsername(), request.getUsername());
        inviteOwnerPlan.connectToGroup(acceptPersonPlan);

        // **Güncellenmiş planları tek seferde kaydediyoruz**
        planRepository.saveAll(List.of(inviteOwnerPlan, acceptPersonPlan));

        log.info("✅ Shared Plan başarıyla oluşturuldu! Owner: {}, Invited: {}, Tarih: {}",
                request.getFirstUsername(), request.getUsername(), request.getPlanDateTime());
    }

    /**
     * Kullanıcının planını getirir, yoksa oluşturur.
     */
    private Plan getOrCreatePlan(String username, LocalDateTime planDateTime) {
        Plan plan = planRepository.findPlanByUsernameAndPlanDate(username, planDateTime.toLocalDate())
                .orElseGet(() -> {
                    Plan newPlan = Plan.builder()
                            .date(planDateTime.toLocalDate())
                            .username(username)
                            .type(PlanType.SHARED)
                            .build();
                    planRepository.save(newPlan);
                    log.info("🆕 Yeni plan oluşturuldu: Kullanıcı: {}, Plan ID: {}", username, newPlan.getId());
                    return newPlan;
                });
        if (plan.getType() == PlanType.NORMAL){
            plan.setType(PlanType.SHARED);
            log.warn("Type changed to shared...");
        }

        return plan;
    }

    private void createSharedPlanRequest(String tokenUsername, InviteStatusRequestDto requestDto, LocalDateTime planDate) {
        KafkaRequest kafkaRequest = KafkaRequest.builder()
                .firstUsername(requestDto.getFirstUsername()) //The person who sent the invitation
                .username(tokenUsername) //accept request username
                .planDateTime(planDate)
                .build();
        log.info("kafkaRequest => {}",kafkaRequest.toString());

        kafkaTemplate.send("create-sharedPlan-topic",kafkaRequest);
    }


}

