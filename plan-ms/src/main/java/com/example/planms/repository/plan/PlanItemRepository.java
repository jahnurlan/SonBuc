package com.example.planms.repository.plan;

import com.example.planms.model.entity.PlanItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface PlanItemRepository extends JpaRepository<PlanItem,Long> {
    @Query("""
    SELECT pi FROM PlanItem pi
    JOIN pi.planContainer pc
    JOIN pc.plan p
    WHERE pi.username = :username
    AND pi.index = :index
    AND pc.index = :containerIndex
    AND p.date = :planDate
""")
    Optional<PlanItem> findByUsernameAndPlanDateAndContainerIndexAndIndex(@Param("username") String username,
                                                                          @Param("planDate") LocalDate planDate,
                                                                          @Param("containerIndex") int containerIndex,
                                                                          @Param("index") int index);

}
