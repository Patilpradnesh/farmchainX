package com.farmchainx.backend.service;

import com.farmchainx.backend.entity.BlockchainRecord;
import com.farmchainx.backend.repository.BlockchainRecordRepository;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

@Service
public class BlockchainService {

    private final BlockchainRecordRepository repository;

    public BlockchainService(BlockchainRecordRepository repository) {
        this.repository = repository;
    }

    public String registerOnBlockchain(String data) {
        String hash = generateHash(data);
        repository.save(new BlockchainRecord(hash, data));
        return hash;
    }

    public BlockchainRecord getRecord(String hash) {
        return repository.findByHash(hash)
                .orElseThrow(() -> new RuntimeException("Blockchain record not found"));
    }

    private String generateHash(String data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encoded = digest.digest(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : encoded) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("Hash generation failed");
        }
    }
}
