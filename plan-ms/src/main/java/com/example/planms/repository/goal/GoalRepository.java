package com.example.planms.repository.goal;

import com.example.planms.model.entity.Goal;
import com.example.planms.model.enums.GoalType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface GoalRepository extends JpaRepository<Goal,Long> {
    @EntityGraph(attributePaths = {"goalContainers"})
    @Query(value = "SELECT g FROM Goal g WHERE g.username = :username AND g.date = :goalDate AND g.goalType = :goalType")
    Optional<Goal> findGoalByUsernameAndPlanDateAndGoalType(
            @Param("username") String username,
            @Param("goalDate") LocalDate goalDate,
            @Param("goalType") GoalType goalType
    );

    Optional<List<Goal>> findAllByUsername(String username);
    List<Goal> findByUsername(String username);

    @Query("SELECT COUNT(g) FROM Goal g")
    long countAllGoals();

    @Query("SELECT COUNT(g) FROM Goal g WHERE g.date = :today")
    long countGoalsByDate(LocalDate today);
}
