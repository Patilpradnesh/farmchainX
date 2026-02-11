package com.farmchainx.backend.config;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.Arrays;

@Component
public class SystemHealthValidator {

    private final ApplicationContext applicationContext;

    public SystemHealthValidator(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void validateSystemHealth() {
        System.out.println("\n🔍 FarmChainX Backend - System Health Validation");
        System.out.println("================================================");

        boolean allHealthy = true;

        // Check Database Connection
        try {
            DataSource dataSource = applicationContext.getBean(DataSource.class);
            try (Connection connection = dataSource.getConnection()) {
                if (connection.isValid(5)) {
                    System.out.println("✅ Database Connection: HEALTHY");
                } else {
                    System.out.println("❌ Database Connection: UNHEALTHY");
                    allHealthy = false;
                }
            }
        } catch (Exception e) {
            System.out.println("❌ Database Connection: FAILED - " + e.getMessage());
            allHealthy = false;
        }

        // Check Critical Beans
        String[] criticalBeans = {
                "jwtUtil",
                "jwtAuthenticationFilter",
                "corsConfigurationSource",
                "passwordEncoder",
                "userRepository",
                "cropRepository",
                "orderRepository",
                "authService",
                "cropService",
                "orderService"
        };

        for (String beanName : criticalBeans) {
            try {
                applicationContext.getBean(beanName);
                System.out.println("✅ Bean '" + beanName + "': LOADED");
            } catch (Exception e) {
                System.out.println("❌ Bean '" + beanName + "': MISSING");
                allHealthy = false;
            }
        }

        // Check Controller Registration
        String[] controllers = applicationContext.getBeanNamesForAnnotation(
                org.springframework.web.bind.annotation.RestController.class);

        if (controllers.length >= 8) {
            System.out.println("✅ Controllers Registered: " + controllers.length + " controllers");
            Arrays.stream(controllers).forEach(c ->
                System.out.println("   📋 " + c));
        } else {
            System.out.println("⚠️  Controllers: Only " + controllers.length + " registered (expected 8+)");
        }

        // Overall System Status
        System.out.println("\n🎯 SYSTEM STATUS");
        System.out.println("================");
        if (allHealthy) {
            System.out.println("🎉 ALL SYSTEMS HEALTHY - READY FOR PRODUCTION!");
            System.out.println("📡 Server: http://localhost:8080");
            System.out.println("🧪 Test: curl http://localhost:8080/api/auth/test");
            System.out.println("❤️  Health: curl http://localhost:8080/health");
            System.out.println("🔐 Admin Login: admin@farmchainx.com / admin123");
        } else {
            System.out.println("⚠️  SYSTEM ISSUES DETECTED - Check logs above");
        }
        System.out.println("================================================\n");
    }
}
