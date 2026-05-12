package com.chronos.repository;

import com.chronos.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // 1. FIND BY EMAIL (For Login)
    // SQL Generated: SELECT * FROM users WHERE email = ?
    Optional<User> findByEmail(String email);

    // 2. FIND BY STATUS (For the Scheduler)
    // SQL Generated: SELECT * FROM users WHERE status = ?
    // This is much faster than fetching ALL users. The scheduler will only fetch "ALIVE" users.
    List<User> findByStatus(String status);
}