package com.farmchainx.backend.blockchain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BlockchainTransactionResponse {
    private String transactionHash;
    private Long blockNumber;
    private String status; // PENDING, CONFIRMED, FAILED
    private String networkName;
    private LocalDateTime timestamp;
    private String explorerUrl;
    private Long gasUsed;
    private String contractAddress;
}

