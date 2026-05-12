package com.chronos.controller;

import com.chronos.service.MockSocialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/mock-social")
@CrossOrigin(origins = "*") // Allows access from any tool/browser
public class MockSocialController {

    @Autowired
    private MockSocialService socialService;

    // ==========================================
    // 1. SIMULATE TWEET (The "Proof of Life")
    // ==========================================
    // Call this using Postman: POST http://localhost:8080/api/mock-social/tweet?email=user@example.com
    @PostMapping("/tweet")
    public ResponseEntity<?> simulateUserActivity(@RequestParam String email) {

        // Log the activity in our Mock Service
        socialService.postTweet(email);

        return ResponseEntity.ok("🐦 Mock Tweet Received! Activity logged for: " + email +
                " at " + LocalDateTime.now());
    }

    // ==========================================
    // 2. CHECK STATUS (For Debugging)
    // ==========================================
    // Call this to see when the user last "Tweeted"
    @GetMapping("/status")
    public ResponseEntity<?> checkUserStatus(@RequestParam String email) {
        LocalDateTime lastActive = socialService.getLastActivity(email);

        if (lastActive != null) {
            return ResponseEntity.ok("Last Social Activity found at: " + lastActive);
        } else {
            return ResponseEntity.ok("No social activity found for this user.");
        }
    }
}