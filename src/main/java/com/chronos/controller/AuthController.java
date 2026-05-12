package com.chronos.controller;

import com.chronos.model.User;
import com.chronos.repository.UserRepository;
import com.chronos.service.AuthService;
import com.chronos.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*") // Allows your HTML frontend to talk to this Java Backend
public class AuthController {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthService authService;

    @Autowired
    private EmailService emailService;

    // ==========================================
    // 1. SIGNUP API (Creates User & Nominee)
    // ==========================================
    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        // Check if email already exists
        if (userRepo.findByEmail(user.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Error: Email is already in use!");
        }

        // 1. Encrypt the password before saving
        user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));

        // 2. Set the "Dead Man's Switch" initial state
        user.setLastCheckIn(LocalDateTime.now());
        user.setStatus("ALIVE");

        // 3. Save to Database
        userRepo.save(user);

        return ResponseEntity.ok("Vault Created Successfully! Monitoring started.");
    }

    // ==========================================
    // 2. LOGIN API (Authenticates & RESETS TIMER)
    // ==========================================
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> data) {
        Optional<User> user = authService.authenticate(data.get("email"), data.get("password"));

        if (user.isPresent()) {
            authService.resetHeartbeat(user.get()); // Logic is now in Service!
            return ResponseEntity.ok("Login Success");
        }
        return ResponseEntity.status(401).body("Invalid Credentials");
    }

    // ==========================================
    // 3. MANUAL CHECK-IN API (The "I'm Alive" Button)
    // ==========================================
    @PostMapping("/check-in")
    public ResponseEntity<?> manualCheckIn(@RequestParam String email) {
        Optional<User> userOpt = userRepo.findByEmail(email);

        if (userOpt.isPresent()) {
            User user = userOpt.get();

            // Reset Timer
            user.setLastCheckIn(LocalDateTime.now());
            user.setStatus("ALIVE");
            userRepo.save(user);

            return ResponseEntity.ok("Check-in Confirmed. See you in " + user.getCheckInThreshold() + " days.");
        }
        return ResponseEntity.badRequest().body("User not found");
    }


    // Add this temporarily to AuthController for testing
    @GetMapping("/test-email")
    public String testEmail() {
        emailService.sendWarningEmail("senthilaravinth0110@gmail.com", "Test User");
        return "Email Sent!";
    }
}