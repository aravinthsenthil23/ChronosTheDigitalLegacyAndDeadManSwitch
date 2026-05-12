package com.chronos.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users") // This creates a table named 'users' in MySQL
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    // --- LOGIN INFO ---
    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String passwordHash; // Stores the BCrypt encrypted password

    // --- THE DEAD MAN'S SWITCH ---

    @Column(name = "last_check_in")
    private LocalDateTime lastCheckIn; // The clock that ticks down

    @Column(name = "check_in_threshold")
    private int checkInThreshold = 30; // Default: 30 Days

    private String status; // Values: "ALIVE", "WARNING", "RELEASED"

    // --- NOMINEE INFO (The person who inherits the data) ---

    private String nomineeName;
    private String nomineeEmail;
    private String nomineePhone;

    // --- CONSTRUCTORS ---
    public User() {}

    public User(String email, String passwordHash, String nomineeEmail) {
        this.email = email;
        this.passwordHash = passwordHash;
        this.nomineeEmail = nomineeEmail;
        this.status = "ALIVE";
        this.lastCheckIn = LocalDateTime.now();
    }

    // --- GETTERS AND SETTERS ---

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public LocalDateTime getLastCheckIn() { return lastCheckIn; }
    public void setLastCheckIn(LocalDateTime lastCheckIn) { this.lastCheckIn = lastCheckIn; }

    public int getCheckInThreshold() { return checkInThreshold; }
    public void setCheckInThreshold(int checkInThreshold) { this.checkInThreshold = checkInThreshold; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getNomineeName() { return nomineeName; }
    public void setNomineeName(String nomineeName) { this.nomineeName = nomineeName; }

    public String getNomineeEmail() { return nomineeEmail; }
    public void setNomineeEmail(String nomineeEmail) { this.nomineeEmail = nomineeEmail; }

    public String getNomineePhone() { return nomineePhone; }
    public void setNomineePhone(String nomineePhone) { this.nomineePhone = nomineePhone; }
}