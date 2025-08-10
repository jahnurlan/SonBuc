package com.example.planms.repository.goal;

import com.example.planms.model.entity.GoalPlanItem;
import com.example.planms.model.entity.PlanItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface GoalPlanItemRepository extends JpaRepository<GoalPlanItem,Long> {
    @Query("""
    SELECT gpi FROM GoalPlanItem gpi
    JOIN gpi.goalContainer gc
    WHERE gpi.username = :username
    AND gpi.index = :index
    AND gc.index = :containerIndex
    AND gc.date = :planDate
""")
    Optional<GoalPlanItem> findByUsernameAndPlanDateAndContainerIndexAndIndex(@Param("username") String username,
                                                                              @Param("planDate") LocalDate planDate,
                                                                              @Param("containerIndex") int containerIndex,
                                                                              @Param("index") int index);

}
