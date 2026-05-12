package com.chronos.repository;

import com.chronos.model.User;
import com.chronos.model.VaultFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VaultFileRepository extends JpaRepository<VaultFile, Long> {

    // 1. FIND FILES BY USER
    // SQL Generated: SELECT * FROM vault_files WHERE user_id = ?
    // Used to show the list of files on the Dashboard
    List<VaultFile> findByUser(User user);

    // Note: We don't need a specific query for "save" or "delete"
    // because JpaRepository provides .save() and .deleteById() automatically.
}