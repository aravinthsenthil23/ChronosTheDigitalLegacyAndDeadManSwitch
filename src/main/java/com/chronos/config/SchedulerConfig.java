package com.chronos.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
@EnableScheduling
public class SchedulerConfig {

    @Bean
    public ThreadPoolTaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();

        // "Pool Size" = How many tasks run at the exact same time.
        // If 5 users need emails, we can handle them in parallel.
        scheduler.setPoolSize(5);

        // This helps you identify these threads in the logs (e.g., "Chronos-Watcher-1")
        scheduler.setThreadNamePrefix("Chronos-Watcher-");

        scheduler.initialize();
        return scheduler;
    }
}