package com.farmchainx.backend.blockchain.controller;

import com.farmchainx.backend.blockchain.dto.BlockchainTransactionRequest;
import com.farmchainx.backend.blockchain.dto.BlockchainTransactionResponse;
import com.farmchainx.backend.blockchain.service.EnhancedBlockchainService;
import com.farmchainx.backend.common.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Blockchain Integration Controller
 */
@RestController
@RequestMapping("/api/v1/blockchain")
public class BlockchainController {

    private final EnhancedBlockchainService blockchainService;

    public BlockchainController(EnhancedBlockchainService blockchainService) {
        this.blockchainService = blockchainService;
    }

    /**
     * Register crop on blockchain (Internal use - called by CropService)
     */
    @PostMapping("/register")
    @PreAuthorize("hasAnyRole('FARMER', 'ADMIN')")
    public ResponseEntity<ApiResponse<BlockchainTransactionResponse>> registerCrop(
            @RequestBody BlockchainTransactionRequest request) {
        try {
            BlockchainTransactionResponse response = blockchainService.registerCropOnBlockchain(request);
            return ResponseEntity.ok(ApiResponse.success("Crop registered on blockchain", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Blockchain registration failed: " + e.getMessage()));
        }
    }

    /**
     * Verify blockchain transaction (Public - for consumer trust)
     */
    @GetMapping("/verify/{transactionHash}")
    public ResponseEntity<ApiResponse<BlockchainTransactionResponse>> verifyTransaction(
            @PathVariable String transactionHash) {
        try {
            BlockchainTransactionResponse response = blockchainService.getTransactionDetails(transactionHash);
            boolean isValid = blockchainService.verifyBlockchainRecord(transactionHash);

            if (isValid) {
                return ResponseEntity.ok(ApiResponse.success("Blockchain record verified", response));
            } else {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("Invalid blockchain record"));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Verification failed: " + e.getMessage()));
        }
    }

    /**
     * Transfer ownership on blockchain
     */
    @PostMapping("/transfer")
    @PreAuthorize("hasAnyRole('FARMER', 'DISTRIBUTOR', 'RETAILER', 'ADMIN')")
    public ResponseEntity<ApiResponse<BlockchainTransactionResponse>> transferOwnership(
            @RequestBody Map<String, Object> transferData) {
        try {
            Long cropId = Long.valueOf(transferData.get("cropId").toString());
            String newOwner = transferData.get("newOwner").toString();

            BlockchainTransactionResponse response = blockchainService.transferOwnership(cropId, newOwner);
            return ResponseEntity.ok(ApiResponse.success("Ownership transferred on blockchain", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Transfer failed: " + e.getMessage()));
        }
    }
}
