package com.example.planms.service.imp;

import com.example.planms.model.dto.response.RankResponseDto;
import com.example.planms.model.dto.response.StatisticsResponseDto;
import com.example.planms.model.entity.Rank;
import com.example.planms.repository.plan.PlanRepository;
import com.example.planms.repository.RankRepository;
import com.example.planms.service.IRankService;
import com.example.planms.util.Helper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class RankService implements IRankService {
    private final RankRepository rankRepository;
    private final PlanRepository planRepository;
    private final Helper helper;

    @Override
    public ResponseEntity<StatisticsResponseDto> getStatisticsByUsername(String jwtToken) {
//        String username = helper.getUsername(jwtToken);
//        Map<String, Integer> planStatusCount = getPlanStatusCount(username);
//
//        log.info("571Finished => " + planStatusCount.get("Finished") + "\nAlmost Done => " + planStatusCount.get("Almost Done") + "\nI didn't => " + planStatusCount.get("I didn't"));
//
//        List<Integer> planDaysWithinCurrentMonth = getPlanDaysWithinCurrentMonth(username);
//        int totalStatusCount = planStatusCount.values().stream().mapToInt(Integer::intValue).sum();
//
//        if (totalStatusCount == 0) {
//            return ResponseEntity.ok().body(StatisticsResponseDto.builder()
//                    .finishedPercentage(0)
//                    .almostFinishedPercentage(0)
//                    .didntPercentage(0)
//                    .activeDays(planDaysWithinCurrentMonth)
//                    .build());
//        }
//
//        int finishedPercentage = calculatePercentage(planStatusCount.get("Finished"), totalStatusCount);
//        int almostFinishedPercentage = calculatePercentage(planStatusCount.get("Almost Done"), totalStatusCount);
//        int didntPercentage = calculatePercentage(planStatusCount.get("I didn't"), totalStatusCount);
//        int noStatusPercentage = calculatePercentage(planStatusCount.get("No status"), totalStatusCount);
//
//        StatisticsResponseDto statisticsResponseDto = StatisticsResponseDto.builder()
//                .finishedPercentage(finishedPercentage)
//                .almostFinishedPercentage(almostFinishedPercentage)
//                .didntPercentage(didntPercentage)
//                .noStatusPercentage(noStatusPercentage)
//                .activeDays(planDaysWithinCurrentMonth)
//                .build();
//
//        log.info("571statisticsResponseDto => " + statisticsResponseDto.toString());
        return ResponseEntity.ok().body(null);
    }

    private int calculatePercentage(int part, int total) {
        return (int) ((double) part / total * 100);
    }

    @Override
    @Transactional
    public ResponseEntity<RankResponseDto> getRankByUsername(String jwtToken) {
        System.out.println("token5711 => " + jwtToken);
        String username = helper.getUsername(jwtToken);
        Optional<Rank> byUsername = rankRepository.findByUsername(username);
        log.info("username => {}", username);

        if (byUsername.isEmpty()){
            log.info("sert ise dusdu hihi");

            Rank newRank = Rank.builder()
                    .username(username)
                    .point(0)
                    .build();
            log.info("getRankByUsername method New rank created and saved => {}", newRank);
            rankRepository.save(newRank);

            RankResponseDto rankResponseDto = RankResponseDto.builder()
                    .rank(rankRepository.countAllBy() + 1)
                    .point(newRank.getPoint())
                    .build();

            return ResponseEntity.ok().body(rankResponseDto);
        }

        Rank rank = byUsername.get();
        RankResponseDto rankResponseDto = RankResponseDto.builder()
                .rank(rankRepository.findRankPositionByPoint(rank.getPoint()))
                .point(rank.getPoint())
                .rank(rankRepository.findRankPositionByPoint(rank.getPoint()))
                .build();

        // Implement as needed
        return ResponseEntity.ok().body(rankResponseDto);
    }

    public void createRank(String username) {
        Optional<Rank> byUsername = rankRepository.findByUsername(username);
        log.info("username => {}", username);

        if (byUsername.isEmpty()){
            log.info("condition is working");

            Rank newRank = Rank.builder()
                    .username(username)
                    .point(0)
                    .build();
            log.info("5711 createRank method New rank created and saved => {}", newRank);
            rankRepository.save(newRank);
        }
    }

    @Override
    public Optional<Rank> findUserRankByUsername(String username) {
        return rankRepository.findByUsername(username);
    }

    public Map<String, Integer> getPlanStatusCount(String username) {
//        LocalDateTime endDate = LocalDateTime.now();
//        LocalDateTime startDate = endDate.minusMonths(1);
//
//        List<Plan> plans = planRepository.findPlansByUsernameAndDateRange(username, startDate, endDate);
//
//        Map<String, Integer> statusCountMap = new HashMap<>();
//        statusCountMap.put("Finished", 0);
//        statusCountMap.put("Almost Done", 0);
//        statusCountMap.put("I didn't", 0);
//        statusCountMap.put("No status", 0);
//
//        plans.parallelStream()
//                .map(Plan::getPlanItemkk)
//                .filter(Objects::nonNull)
//                .forEach(planItem -> {
//                    List<String> statuses = Arrays.asList(
//                            planItem.getImp1Status(),
//                            planItem.getImp2Status(),
//                            planItem.getImp3Status(),
//                            planItem.getReq1Status(),
//                            planItem.getReq2Status(),
//                            planItem.getReq3Status(),
//                            planItem.getNice1Status(),
//                            planItem.getNice2Status(),
//                            planItem.getNice3Status()
//                    );
//                    log.info("PlanItemkk 888 => {}",planItem);
//
//                    statuses.stream()
//                            .filter(Objects::nonNull)
//                            .forEach(status -> statusCountMap.merge(status, 1, Integer::sum));
//
//                    checkNoStatusProcess(planItem, statusCountMap);
//
//                });
//
//        return statusCountMap;
        return null;
    }

    private void checkNoStatusProcess( Map<String, Integer> statusCountMap) {
//        if (checkPlanItemText(planItemkk.getImp1()) && planItemkk.getImp1Status() == null){
//            statusCountMap.merge("No status", 1, Integer::sum);
//        }
//        if (checkPlanItemText(planItemkk.getImp2()) && planItemkk.getImp2Status() == null){
//            statusCountMap.merge("No status", 1, Integer::sum);
//        }
//        if (checkPlanItemText(planItemkk.getImp3()) && planItemkk.getImp3Status() == null){
//            statusCountMap.merge("No status", 1, Integer::sum);
//        }
//        if (checkPlanItemText(planItemkk.getReq1()) && planItemkk.getReq1Status() == null){
//            statusCountMap.merge("No status", 1, Integer::sum);
//        }
//        if (checkPlanItemText(planItemkk.getReq2()) && planItemkk.getReq2Status() == null){
//            statusCountMap.merge("No status", 1, Integer::sum);
//        }
//        if (checkPlanItemText(planItemkk.getReq3()) && planItemkk.getReq3Status() == null){
//            statusCountMap.merge("No status", 1, Integer::sum);
//        }
//        if (checkPlanItemText(planItemkk.getNice1()) && planItemkk.getNice1Status() == null){
//            statusCountMap.merge("No status", 1, Integer::sum);
//        }
//        if (checkPlanItemText(planItemkk.getNice2()) && planItemkk.getNice2Status() == null){
//            statusCountMap.merge("No status", 1, Integer::sum);
//        }
//        if (checkPlanItemText(planItemkk.getNice3()) && planItemkk.getNice3Status() == null){
//            statusCountMap.merge("No status", 1, Integer::sum);
//        }
    }

    private boolean checkPlanItemText(String text) {
        return text != null && !text.isEmpty();
    }

//    public List<Integer> getPlanDaysWithinCurrentMonth(String username) {
//        LocalDateTime startDate = LocalDateTime.now().withDayOfMonth(1); // Ayın başlangıcı
//        LocalDateTime endDate = LocalDateTime.now().plusDays(1); // Şu anki tarih
//
//        List<Integer> days = planRepository.findPlanDaysWithinCurrentMonth(username,startDate, endDate);
//        List<Integer> collect = days.stream().sorted().collect(Collectors.toList());
//        System.out.println("571Days List => " + collect);
//        return collect; // Gün değerlerini sıralı olarak döndür
//    }


}
