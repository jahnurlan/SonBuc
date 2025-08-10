package com.example.chatms.repository;

import com.example.chatms.model.entity.PlanContainer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlanContainerRepository extends JpaRepository<PlanContainer,Long> {

}
