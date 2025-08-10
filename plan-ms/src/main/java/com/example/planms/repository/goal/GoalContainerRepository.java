package com.example.planms.repository.goal;

import com.example.planms.model.entity.GoalContainer;
import com.example.planms.model.entity.PlanContainer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface GoalContainerRepository extends JpaRepository<GoalContainer, Long> {
    @Query("SELECT g FROM GoalContainer g WHERE g.username = :username AND g.date = :date AND g.index = :index")
    Optional<GoalContainer> findByUsernameAndDateAndIndex(@Param("username") String username,
                                                          @Param("date") LocalDate date,
                                                          @Param("index") int index);
}



