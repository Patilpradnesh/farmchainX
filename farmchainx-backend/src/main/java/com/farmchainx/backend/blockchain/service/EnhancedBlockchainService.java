package com.farmchainx.backend.blockchain.service;

import com.farmchainx.backend.blockchain.dto.BlockchainTransactionRequest;
import com.farmchainx.backend.blockchain.dto.BlockchainTransactionResponse;
import com.farmchainx.backend.entity.BlockchainRecord;
import com.farmchainx.backend.entity.Crop;
import com.farmchainx.backend.repository.BlockchainRecordRepository;
import com.farmchainx.backend.repository.CropRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;

/**
 * Enhanced Blockchain Service - Ready for real Web3j integration
 * Currently uses simulated blockchain for development
 * TODO: Replace with actual Web3j implementation for production
 */
@Service
public class EnhancedBlockchainService {

    private final BlockchainRecordRepository blockchainRecordRepository;
    private final CropRepository cropRepository;

    @Value("${blockchain.network:sepolia}")
    private String networkName;

    @Value("${blockchain.contract.address:0x0000000000000000000000000000000000000000}")
    private String contractAddress;

    @Value("${blockchain.enabled:false}")
    private boolean blockchainEnabled;

    public EnhancedBlockchainService(BlockchainRecordRepository blockchainRecordRepository,
                                    CropRepository cropRepository) {
        this.blockchainRecordRepository = blockchainRecordRepository;
        this.cropRepository = cropRepository;
    }

    /**
     * Register crop on blockchain
     * In production: This will call smart contract's registerCropBatch() function
     */
    @Transactional
    public BlockchainTransactionResponse registerCropOnBlockchain(BlockchainTransactionRequest request) {

        if (blockchainEnabled) {
            // TODO: Real Web3j implementation
            return registerOnRealBlockchain(request);
        } else {
            // Simulated blockchain for development
            return registerOnSimulatedBlockchain(request);
        }
    }

    /**
     * Verify blockchain record
     */
    public boolean verifyBlockchainRecord(String transactionHash) {
        if (blockchainEnabled) {
            // TODO: Query actual blockchain
            return verifyOnRealBlockchain(transactionHash);
        } else {
            // Verify in local database
            return blockchainRecordRepository.findByTransactionHash(transactionHash).isPresent();
        }
    }

    /**
     * Get blockchain transaction details
     */
    public BlockchainTransactionResponse getTransactionDetails(String transactionHash) {
        BlockchainRecord record = blockchainRecordRepository.findByTransactionHash(transactionHash)
                .orElseThrow(() -> new RuntimeException("Blockchain record not found"));

        BlockchainTransactionResponse response = new BlockchainTransactionResponse();
        response.setTransactionHash(record.getTransactionHash());
        response.setBlockNumber(record.getBlockNumber());
        response.setStatus("CONFIRMED");
        response.setNetworkName(networkName);
        response.setTimestamp(record.getCreatedAt());
        response.setContractAddress(contractAddress);

        if (blockchainEnabled) {
            response.setExplorerUrl(getExplorerUrl(transactionHash));
        }

        return response;
    }

    /**
     * Transfer crop ownership on blockchain
     */
    @Transactional
    public BlockchainTransactionResponse transferOwnership(Long cropId, String newOwnerAddress) {
        Crop crop = cropRepository.findById(cropId)
                .orElseThrow(() -> new RuntimeException("Crop not found"));

        // Create transfer transaction data
        String transferData = String.format("TRANSFER|%d|%s|%s",
                cropId, crop.getBlockchainHash(), newOwnerAddress);

        String txHash = generateTransactionHash(transferData);

        // Save blockchain record
        BlockchainRecord record = new BlockchainRecord();
        record.setTransactionHash(txHash);
        record.setTransactionType("OWNERSHIP_TRANSFER");
        record.setCropId(cropId);
        record.setBlockNumber(System.currentTimeMillis() / 1000); // Simulated block number
        blockchainRecordRepository.save(record);

        BlockchainTransactionResponse response = new BlockchainTransactionResponse();
        response.setTransactionHash(txHash);
        response.setBlockNumber(record.getBlockNumber());
        response.setStatus("CONFIRMED");
        response.setNetworkName(networkName);
        response.setTimestamp(LocalDateTime.now());
        response.setContractAddress(contractAddress);

        return response;
    }

    // ========== PRIVATE METHODS ==========

    private BlockchainTransactionResponse registerOnSimulatedBlockchain(BlockchainTransactionRequest request) {
        // Create unique transaction data
        String transactionData = String.format("%s|%s|%s|%s|%.2f|%s",
                request.getCropId(),
                request.getCropName(),
                request.getFarmerAddress(),
                request.getLocation(),
                request.getQuantity(),
                request.getHarvestDate()
        );

        String txHash = generateTransactionHash(transactionData);
        long blockNumber = System.currentTimeMillis() / 1000; // Simulated block number

        // Save to database
        BlockchainRecord record = new BlockchainRecord();
        record.setTransactionHash(txHash);
        record.setTransactionType("CROP_REGISTRATION");
        record.setCropId(request.getCropId());
        record.setBlockNumber(blockNumber);
        blockchainRecordRepository.save(record);

        // Create response
        BlockchainTransactionResponse response = new BlockchainTransactionResponse();
        response.setTransactionHash(txHash);
        response.setBlockNumber(blockNumber);
        response.setStatus("CONFIRMED");
        response.setNetworkName("SIMULATED");
        response.setTimestamp(LocalDateTime.now());
        response.setContractAddress("0xSIMULATED");
        response.setGasUsed(21000L);

        System.out.println("✅ Blockchain Record Created (Simulated):");
        System.out.println("   TxHash: " + txHash);
        System.out.println("   Block: " + blockNumber);
        System.out.println("   Crop ID: " + request.getCropId());

        return response;
    }

    private BlockchainTransactionResponse registerOnRealBlockchain(BlockchainTransactionRequest request) {
        /*
         * TODO: Real Web3j Implementation
         *
         * Steps:
         * 1. Load Web3j instance
         * 2. Load contract ABI
         * 3. Call registerCropBatch(cropId, farmerAddress, location, quantity, harvestDate)
         * 4. Wait for transaction confirmation
         * 5. Return actual transaction hash and block number
         *
         * Example code:
         *
         * Web3j web3j = Web3j.build(new HttpService(ethereumNodeUrl));
         * Credentials credentials = Credentials.create(privateKey);
         * CropRegistry contract = CropRegistry.load(contractAddress, web3j, credentials, gasPrice, gasLimit);
         *
         * TransactionReceipt receipt = contract.registerCropBatch(
         *     BigInteger.valueOf(request.getCropId()),
         *     request.getFarmerAddress(),
         *     request.getLocation(),
         *     BigInteger.valueOf(request.getQuantity()),
         *     request.getHarvestDate()
         * ).send();
         *
         * return new BlockchainTransactionResponse(
         *     receipt.getTransactionHash(),
         *     receipt.getBlockNumber(),
         *     "CONFIRMED",
         *     networkName,
         *     LocalDateTime.now(),
         *     getExplorerUrl(receipt.getTransactionHash()),
         *     receipt.getGasUsed(),
         *     contractAddress
         * );
         */

        throw new UnsupportedOperationException(
                "Real blockchain integration not yet configured. " +
                "Set blockchain.enabled=true and configure Web3j dependencies."
        );
    }

    private boolean verifyOnRealBlockchain(String transactionHash) {
        /*
         * TODO: Query blockchain network
         *
         * Web3j web3j = Web3j.build(new HttpService(ethereumNodeUrl));
         * EthGetTransactionReceipt receipt = web3j.ethGetTransactionReceipt(transactionHash).send();
         * return receipt.getTransactionReceipt().isPresent();
         */
        return false;
    }

    private String generateTransactionHash(String data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encoded = digest.digest(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder("0x");
            for (byte b : encoded) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate transaction hash", e);
        }
    }

    private String getExplorerUrl(String transactionHash) {
        // Etherscan URLs for different networks
        switch (networkName.toLowerCase()) {
            case "mainnet":
                return "https://etherscan.io/tx/" + transactionHash;
            case "sepolia":
                return "https://sepolia.etherscan.io/tx/" + transactionHash;
            case "goerli":
                return "https://goerli.etherscan.io/tx/" + transactionHash;
            default:
                return "https://etherscan.io/tx/" + transactionHash;
        }
    }
}
