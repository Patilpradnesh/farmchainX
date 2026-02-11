package com.farmchainx.backend.repository;

import com.farmchainx.backend.entity.BlockchainRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BlockchainRecordRepository extends JpaRepository<BlockchainRecord, Long> {

    Optional<BlockchainRecord> findByTransactionHash(String transactionHash);

    Optional<BlockchainRecord> findByCropId(Long cropId);
}
