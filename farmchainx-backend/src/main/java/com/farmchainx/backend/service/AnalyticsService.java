package com.farmchainx.backend.service;

import com.farmchainx.backend.repository.CropRepository;
import com.farmchainx.backend.repository.CropHistoryRepository;
import com.farmchainx.backend.repository.OrderRepository;
import org.springframework.stereotype.Service;

@Service
public class AnalyticsService {

    private final CropRepository cropRepository;
    private final CropHistoryRepository cropHistoryRepository;
    private final OrderRepository orderRepository;

    public AnalyticsService(CropRepository cropRepository, CropHistoryRepository cropHistoryRepository, OrderRepository orderRepository) {
        this.cropRepository = cropRepository;
        this.cropHistoryRepository = cropHistoryRepository;
        this.orderRepository = orderRepository;
    }

    public Long getTotalCrops() {
        return cropRepository.count();
    }

    public Long getTotalStateChanges() {
        return cropHistoryRepository.countStateChanges();
    }

    public Long getTotalOwnershipTransfers() {
        return cropHistoryRepository.countOwnershipTransfers();
    }

    public Long getTotalOrders() {
        return orderRepository.count();
    }
}
