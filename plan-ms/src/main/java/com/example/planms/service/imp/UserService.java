package com.example.planms.service.imp;

import com.example.planms.model.dto.request.InviteUserRequestDto;
import com.example.planms.model.dto.request.KafkaRequest;
import com.example.planms.model.dto.response.InvitedUserResponseDto;
import com.example.planms.model.entity.InviteAnnounce;
import com.example.planms.model.entity.Rank;
import com.example.planms.repository.InviteAnnounceRepository;
import com.example.planms.repository.RankRepository;
import com.example.planms.service.IRankService;
import com.example.planms.service.IUserService;
import com.example.planms.util.Helper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService implements IUserService {
    private final IRankService rankService;
    private final Helper helper;
    private final KafkaTemplate<String, KafkaRequest> kafkaTemplate;
    private final InviteAnnounceRepository inviteAnnounceRepository;
    private final RankRepository rankRepository;

    @Override
    public ResponseEntity<InvitedUserResponseDto> findUser(String username,String jwtToken) {
        String firstUsername = helper.getUsername(jwtToken);
        Optional<Rank> userRankByUsername = rankService.findUserRankByUsername(username);
        log.info("findUser Method working => {}",firstUsername);

        if (userRankByUsername.isPresent() ){
            if (userRankByUsername.get().getUsername().equals(firstUsername)){
                return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
            }

            Rank rank = userRankByUsername.get();

            InvitedUserResponseDto responseDto = InvitedUserResponseDto.builder()
                    .username(username)
                    .rank(rankRepository.findRankPositionByPoint(rank.getPoint()))
                    .level(helper.findLevel(rank.getPoint()))
                    .build();
            log.info("user founded => {}",responseDto);
            return ResponseEntity.ok().body(responseDto);
        }else {
            log.info("User not founded");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @Override
    public ResponseEntity<String> inviteUserRequest(InviteUserRequestDto inviteUserRequestDto, String jwtToken) {
        String firstUsername = helper.getUsername(jwtToken);

        Optional<InviteAnnounce> announceByUsernameAndPlanDate = inviteAnnounceRepository.findAnnounceByUsernameAndPlanDate(firstUsername,inviteUserRequestDto.getUsername(), inviteUserRequestDto.getPlanDate().toLocalDate());

        if (announceByUsernameAndPlanDate.isPresent()){
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("User already requested");
        } else {
            KafkaRequest kafkaRequest = KafkaRequest.builder()
                    .username(inviteUserRequestDto.getUsername())
                    .firstUsername(firstUsername)
                    .planDateTime(inviteUserRequestDto.getPlanDate())
                    .build();
            log.info("kafkaRequest => {}",kafkaRequest.toString());

            kafkaTemplate.send("user-invite-topic",kafkaRequest);

            return ResponseEntity.ok().body(null);
        }
    }

    @Override
    public void inviteUser(KafkaRequest kafkaRequest) {
        InviteAnnounce inviteAnnounce = InviteAnnounce.builder()
                .announceDate(LocalDateTime.now())
                .invited_username(kafkaRequest.getUsername())
                .username(kafkaRequest.getFirstUsername())
                .planDate(kafkaRequest.getPlanDateTime())
                .build();
        inviteAnnounceRepository.save(inviteAnnounce);
    }

}

