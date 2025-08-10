package com.example.planms.repository.goal;

import com.example.planms.model.entity.GoalItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GoalItemRepository extends JpaRepository<GoalItem,Long> {

}
