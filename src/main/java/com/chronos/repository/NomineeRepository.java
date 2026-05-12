package com.chronos.repository;

import com.chronos.model.Nominee;
import com.chronos.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NomineeRepository extends JpaRepository<Nominee, Long> {

    // 1. FIND NOMINEES BY USER
    // SQL Generated: SELECT * FROM nominees WHERE user_id = ?
    List<Nominee> findByUser(User user);

    // 2. FIND BY ACCESS TOKEN (The "Key" to the Vault)
    // SQL Generated: SELECT * FROM nominees WHERE access_token = ?
    // This is used when the Nominee clicks the "Download Legacy" link.
    Optional<Nominee> findByAccessToken(String accessToken);
}