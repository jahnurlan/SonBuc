package com.example.userms.repository;

import com.example.userms.model.entity.User;
import com.example.userms.model.enums.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    @Query(value = "select * from _user where username = :username or email = :username",nativeQuery = true)
    Optional<User> findUserByUsernameOrEmail(@Param("username") String username);

    Optional<User> findUserByUsername(String username);
    Optional<User> findUserByEmail(String email);
    Optional<User> findUserById(Long id);

    @Query("SELECT COUNT(u) FROM User u WHERE u.role.name = :roleType")
    long countUsersByRole(RoleType roleType);

    @Query("SELECT COUNT(u) FROM User u WHERE u.role.name = :roleType AND DATE(u.joinDate) = :today")
    long countByRoleAndJoinDate(RoleType roleType, LocalDate today);

    @Query(value = "SELECT COUNT(*) FROM _user u " +
            "JOIN role r ON u.role_id = r.id " +
            "WHERE r.name = :roleType " +
            "AND DATE(u.join_date) = CURRENT_DATE", nativeQuery = true)
    long countTodayUsersWithUserRoleNative(@Param("roleType") String roleType);

    @Query("SELECT COUNT(u) FROM User u WHERE u.enabled = false")
    long countInactiveUsers();

}
