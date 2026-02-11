package com.farmchainx.backend.repository;

import com.farmchainx.backend.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    // Add custom queries if needed
}
