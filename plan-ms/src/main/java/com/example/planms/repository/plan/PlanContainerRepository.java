package com.example.planms.repository.plan;

import com.example.planms.model.entity.PlanContainer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PlanContainerRepository extends JpaRepository<PlanContainer, Long> {
    @Query("""
        SELECT pc FROM PlanContainer pc
        JOIN pc.plan p
        WHERE p.username = :username
          AND p.date = :planDate
          AND pc.index = :index
    """)
    Optional<PlanContainer> findByUsernameAndDateAndIndex(
            @Param("username") String username,
            @Param("planDate") LocalDate planDate,
            @Param("index") int index
    );

    List<PlanContainer> findAllByUsernameAndPlanDate(String username, LocalDate planDate);
}



