package com.farmchainx.backend.repository;

import com.farmchainx.backend.entity.BlockchainRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BlockchainRecordRepository extends JpaRepository<BlockchainRecord, Long> {
    Optional<BlockchainRecord> findByHash(String hash);
}
