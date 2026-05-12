package com.chronos.service;

import com.chronos.model.User;
import com.chronos.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // ==========================================
    // 1. REGISTER NEW USER
    // ==========================================
    public String registerUser(User user) {
        // Check if email already exists
        if (userRepo.findByEmail(user.getEmail()).isPresent()) {
            return "Error: Email already exists.";
        }

        // Hash the password (Security Best Practice)
        user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));

        // Set Default "Dead Man's Switch" Values
        user.setLastCheckIn(LocalDateTime.now());
        user.setStatus("ALIVE");
        if (user.getCheckInThreshold() == 0) {
            user.setCheckInThreshold(30); // Default to 30 days if not set
        }

        userRepo.save(user);
        return "Success";
    }

    // ==========================================
    // 2. AUTHENTICATE USER
    // ==========================================
    public Optional<User> authenticate(String email, String rawPassword) {
        Optional<User> userOpt = userRepo.findByEmail(email);

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            // Compare raw password with Hashed password in DB
            if (passwordEncoder.matches(rawPassword, user.getPasswordHash())) {
                return Optional.of(user);
            }
        }
        return Optional.empty(); // Login Failed
    }

    // ==========================================
    // 3. RESET HEARTBEAT (The "I'm Alive" Logic)
    // ==========================================
    public void resetHeartbeat(User user) {
        user.setLastCheckIn(LocalDateTime.now());

        // If they were in "WARNING" mode, bring them back to "ALIVE"
        if ("WARNING".equals(user.getStatus())) {
            user.setStatus("ALIVE");
        }

        userRepo.save(user);
        System.out.println("💓 Heartbeat Reset for User: " + user.getEmail());
    }
}