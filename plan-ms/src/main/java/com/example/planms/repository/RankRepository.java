package com.example.planms.repository;

import com.example.planms.model.entity.Rank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RankRepository extends JpaRepository<Rank,Long> {
    @Query(value = "select * from rank where username = :username", nativeQuery = true)
    Optional<Rank> findByUsername(@Param("username") String username);
    long countAllBy();

    @Query(value = "SELECT COUNT(*) + 1 AS rank_position FROM rank WHERE point > :userPoint", nativeQuery = true)
    int findRankPositionByPoint(@Param("userPoint") int userPoint);
}




