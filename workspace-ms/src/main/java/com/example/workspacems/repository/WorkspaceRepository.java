package com.example.workspacems.repository;

import com.example.workspacems.model.entity.Workspace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface WorkspaceRepository extends JpaRepository<Workspace, Long> {
    List<Workspace> findAllByUsername(String username);

    @Query("SELECT COUNT(w) FROM Workspace w")
    long countAllWorkspaces();

    @Query("SELECT COUNT(w) FROM Workspace w WHERE DATE(w.createdAt) = :today")
    long countWorkspacesCreatedToday(LocalDate today);

}
