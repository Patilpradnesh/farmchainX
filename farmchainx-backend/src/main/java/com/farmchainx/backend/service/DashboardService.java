package com.farmchainx.backend.service;

import com.farmchainx.backend.dto.FarmerDashboardResponse;
import com.farmchainx.backend.entity.Crop;
import com.farmchainx.backend.repository.CropRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DashboardService {

    private final CropRepository cropRepository;

    public DashboardService(CropRepository cropRepository) {
        this.cropRepository = cropRepository;
    }

    public FarmerDashboardResponse getFarmerDashboard() {

        String email =
                SecurityContextHolder.getContext()
                        .getAuthentication()
                        .getName();

        List<Crop> crops =
                cropRepository.findByFarmerEmail(email);

        var summaries =
                crops.stream()
                        .map(c ->
                                new FarmerDashboardResponse.CropSummary(
                                        c.getId(),
                                        c.getName(),
                                        c.getBlockchainHash()   // ✅ CORRECT
                                )

                        )
                        .toList();

        return new FarmerDashboardResponse(
                email,
                "ACTIVE",
                summaries
        );
    }
}
