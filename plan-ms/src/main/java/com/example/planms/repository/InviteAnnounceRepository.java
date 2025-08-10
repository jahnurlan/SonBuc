package com.example.planms.repository;

import com.example.planms.model.entity.InviteAnnounce;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface InviteAnnounceRepository extends JpaRepository<InviteAnnounce,Long> {
    @Query("SELECT COUNT(i) FROM InviteAnnounce i WHERE i.invited_username = :username AND i.status = true AND i.readingStatus = false")
    long countByUsernameAndStatusTrue(@Param("username") String username);

    @Query("SELECT i FROM InviteAnnounce i WHERE i.invited_username = :username AND i.status = true")
    List<InviteAnnounce> getAllActiveAnnounce(@Param("username") String username);

    @Query("SELECT i FROM InviteAnnounce i WHERE i.invited_username = :username AND i.invited_username = :secondUsername AND DATE(i.planDate) = :planDate")
    Optional<InviteAnnounce> findAnnounceByUsernameAndPlanDate(
            @Param("username") String firstUsername,
            @Param("secondUsername") String secondUsername,
            @Param("planDate") LocalDate planDate);

    @Modifying
    @Query("UPDATE InviteAnnounce i SET i.readingStatus = true WHERE i.id IN :ids")
    int updateReadingStatusByIdsInBatch(@Param("ids") List<Long> ids);


    @Query("SELECT i FROM InviteAnnounce i WHERE i.username = :firstUsername AND i.invited_username = :username AND i.id = :id")
    Optional<InviteAnnounce> findAnnounceByUsernameAndId(String firstUsername, String username, Long id);
}

