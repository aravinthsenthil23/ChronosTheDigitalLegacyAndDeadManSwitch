package com.chronos.Schedular;

import com.chronos.model.Nominee;
import com.chronos.model.User;
import com.chronos.repository.NomineeRepository;
import com.chronos.repository.UserRepository;
import com.chronos.service.EmailService;
import com.chronos.service.MockSocialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Component
public class HeartbeatScheduler {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private NomineeRepository nomineeRepo;

    @Autowired
    private EmailService emailService;

    @Autowired
    private MockSocialService socialService;

    // Runs every minute for Demo purposes. (Change to "0 0 0 * * ?" for daily)
    @Scheduled(cron = "0 * * * * ?")
    public void checkHeartbeats() {
        System.out.println("⏰ Scheduler Running: Checking for inactive users...");

        // Fetch only users who are NOT yet released
        List<User> users = userRepo.findAll();

        LocalDateTime now = LocalDateTime.now();

        for (User user : users) {
            // Skip if already released
            if ("RELEASED".equals(user.getStatus())) continue;

            long daysInactive = ChronoUnit.DAYS.between(user.getLastCheckIn(), now);
            int threshold = user.getCheckInThreshold();

            // ====================================================
            // PHASE 1: THE X-FACTOR (Social Media Rescue)
            // ====================================================
            // If user is inactive for > 20 days, check "Twitter" before panicking
            if (daysInactive > 20) {
                LocalDateTime lastSocialPost = socialService.getLastActivity(user.getEmail());

                // If they posted on social media AFTER their last Chronos Login
                if (lastSocialPost != null && lastSocialPost.isAfter(user.getLastCheckIn())) {
                    System.out.println("♻️ AUTO-RESET: Social activity detected for " + user.getEmail());

                    user.setLastCheckIn(lastSocialPost); // Update time
                    user.setStatus("ALIVE");
                    userRepo.save(user);
                    continue; // Skip to next user
                }
            }

            // ====================================================
            // PHASE 2: WARNING STAGE
            // ====================================================
            // e.g., If Threshold is 30, warn at 27, 28, 29
            if (daysInactive >= (threshold - 3) && daysInactive < threshold) {

                // Only send if status isn't already WARNING (to avoid spamming)
                if (!"WARNING".equals(user.getStatus())) {
                    emailService.sendWarningEmail(user.getEmail(), user.getEmail());

                    user.setStatus("WARNING");
                    userRepo.save(user);
                    System.out.println("⚠️ Warning sent to: " + user.getEmail());
                }
            }

            // ====================================================
            // PHASE 3: CRITICAL RELEASE (Dead Man's Switch Trigger)
            // ====================================================
            if (daysInactive >= threshold) {
                triggerRelease(user);
            }
        }
    }

    private void triggerRelease(User user) {
        System.out.println("🚨 RELEASE TRIGGERED FOR: " + user.getEmail());

        // 1. Generate Access Token (Secure Random String)
        String token = UUID.randomUUID().toString();

        // 2. Update Nominee with Token
        List<Nominee> nominees = nomineeRepo.findByUser(user);
        for (Nominee nominee : nominees) {
            nominee.setAccessToken(token);
            nomineeRepo.save(nominee);

            // 3. Email the Nominee
            emailService.sendReleaseEmail(nominee.getEmail(), user.getEmail(), token);
        }

        // 4. Update User Status
        user.setStatus("RELEASED");
        userRepo.save(user);
    }
}