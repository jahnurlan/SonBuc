package com.example.workspacems.repository;

import com.example.workspacems.model.entity.ScheduledDay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScheduledDayRepository extends JpaRepository<ScheduledDay, Long> {
    List<ScheduledDay> findAllByWorkspace_Id(Long workspaceId);
}
