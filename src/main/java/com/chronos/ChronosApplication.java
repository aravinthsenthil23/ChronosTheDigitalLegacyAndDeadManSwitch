package com.chronos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling // <--- THIS IS THE MAGIC SWITCH. It activates the background timer.
public class ChronosApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChronosApplication.class, args);
        System.out.println("⏳ Chronos System is Online. Monitoring Heartbeats...");
    }
}