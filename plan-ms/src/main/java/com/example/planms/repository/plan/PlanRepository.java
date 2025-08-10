package com.example.planms.repository.plan;

import com.example.planms.model.entity.Plan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PlanRepository extends JpaRepository<Plan, Long> {
    // JPQL ile son 20 günün planlarını döner
    @Query("SELECT p FROM Plan p WHERE p.username = :username AND p.date >= :startDate ORDER BY p.date DESC")
    List<Plan> findPlansFromLastDays(LocalDate startDate, @Param("username") String username);

    @Query("SELECT i FROM Plan i WHERE i.username = :username AND DATE(i.date) = :planDate")
    Optional<Plan> findPlanByUsernameAndPlanDate(
            @Param("username") String username,
            @Param("planDate") LocalDate planDate);

    @Query("SELECT p FROM Plan p " +
            "JOIN p.connectedPlans cp " +
            "WHERE p.username = :firstUsername " +
            "AND p.type = 'SHARED' " +
            "AND cp.username = :secondUsername")
    List<Plan> findSharedPlansByUsernames(@Param("firstUsername") String firstUsername,
                                          @Param("secondUsername") String secondUsername);

    @Query("SELECT COUNT(p) FROM Plan p")
    long countAllPlans();

    @Query("SELECT COUNT(p) FROM Plan p WHERE p.date = :today")
    long countPlansByDate(LocalDate today);

    @Query("SELECT COUNT(p) FROM Plan p WHERE p.goal IS NOT NULL")
    long countPlansWithGoal();

    @Query("SELECT COUNT(p) FROM Plan p WHERE p.goal IS NOT NULL AND p.date = :today")
    long countTodayPlansWithGoal(LocalDate today);


}



