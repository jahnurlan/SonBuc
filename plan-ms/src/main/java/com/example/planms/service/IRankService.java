package com.example.planms.service;

import com.example.planms.model.dto.response.RankResponseDto;
import com.example.planms.model.dto.response.StatisticsResponseDto;
import com.example.planms.model.entity.Rank;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

public interface IRankService {
    ResponseEntity<StatisticsResponseDto> getStatisticsByUsername(String jwtToken);

    ResponseEntity<RankResponseDto> getRankByUsername(String jwtToken);
    Optional<Rank> findUserRankByUsername(String username);
    void createRank(String username);
}
