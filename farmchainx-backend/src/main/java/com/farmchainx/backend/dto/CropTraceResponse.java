package com.farmchainx.backend.dto;

import java.util.List;

public record CropTraceResponse(
        String cropName,
        String blockchainHash,
        String cropState,
        String createdAt,
        String currentOwnerEmail,
        List<String> history
) {}
