package com.farmchainx.backend.blockchain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BlockchainTransactionRequest {
    private Long cropId;
    private String cropName;
    private String farmerAddress;
    private String location;
    private Double quantity;
    private String harvestDate;
    private String certificateHash;
}
