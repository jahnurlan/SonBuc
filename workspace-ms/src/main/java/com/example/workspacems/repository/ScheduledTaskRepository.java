package com.example.workspacems.repository;

import com.example.workspacems.model.entity.ScheduledDayTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScheduledTaskRepository extends JpaRepository<ScheduledDayTask, Long> {
}
