package com.example.workspacems.repository;

import com.example.workspacems.model.entity.WorkspaceTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkspaceTaskRepository extends JpaRepository<WorkspaceTask, Long> {
}
