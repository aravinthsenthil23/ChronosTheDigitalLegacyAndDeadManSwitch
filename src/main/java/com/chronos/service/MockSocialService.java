package com.chronos.service;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class MockSocialService {

    // Simulates a database table for Social Media Activity
    // Key: User Email | Value: Timestamp of last "Tweet"
    private final Map<String, LocalDateTime> socialActivityLog = new HashMap<>();

    // ==========================================
    // 1. POST A MOCK TWEET (Simulate Activity)
    // ==========================================
    // This is called by the MockSocialController when you hit the endpoint.
    public void postTweet(String email) {
        LocalDateTime now = LocalDateTime.now();
        socialActivityLog.put(email, now);

        System.out.println("🐦 [MOCK TWITTER] New Post Detected!");
        System.out.println("   User: " + email);
        System.out.println("   Time: " + now);
    }

    // ==========================================
    // 2. GET LAST ACTIVITY (For the Scheduler)
    // ==========================================
    // The HeartbeatScheduler calls this to check if the user is alive.
    public LocalDateTime getLastActivity(String email) {
        return socialActivityLog.getOrDefault(email, null);
    }

    // ==========================================
    // 3. DEBUGGING HELPER (Optional)
    // ==========================================
    public Map<String, LocalDateTime> getAllLogs() {
        return socialActivityLog;
    }
}