package com.farmchainx.backend.service;

import com.farmchainx.backend.dto.*;
import com.farmchainx.backend.entity.Crop;
import com.farmchainx.backend.entity.Order;
import com.farmchainx.backend.entity.User;
import com.farmchainx.backend.enums.OrderState;
import com.farmchainx.backend.repository.CropRepository;
import com.farmchainx.backend.repository.OrderRepository;
import com.farmchainx.backend.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    private final CropRepository cropRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    public DashboardService(CropRepository cropRepository, OrderRepository orderRepository, UserRepository userRepository) {
        this.cropRepository = cropRepository;
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
    }

    public FarmerDashboardResponse getFarmerDashboard() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        List<Crop> crops = cropRepository.findByCurrentOwnerEmail(email);

        var summaries = crops.stream()
                .map(c -> new FarmerDashboardResponse.CropSummary(
                        c.getId(),
                        c.getCropName(),
                        c.getBlockchainHash()
                ))
                .toList();

        return new FarmerDashboardResponse(email, "ACTIVE", summaries);
    }

    public DistributorDashboardResponse getDistributorDashboard() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Calculate stats
        List<Order> purchases = orderRepository.findByBuyer(user);
        List<Order> sales = orderRepository.findBySeller(user);
        long activeOrders = purchases.stream()
                .filter(o -> o.getOrderState() != OrderState.COMPLETED && o.getOrderState() != OrderState.CANCELLED)
                .count();
        double totalRevenue = sales.stream()
                .filter(o -> o.getOrderState() == OrderState.COMPLETED)
                .mapToDouble(o -> 0.0) // Price calculation would go here
                .sum();

        DistributorDashboardResponse.DashboardStats stats = new DistributorDashboardResponse.DashboardStats(
                purchases.size(), sales.size(), activeOrders, totalRevenue
        );

        // Recent purchases
        List<DistributorDashboardResponse.PurchaseSummary> recentPurchases = purchases.stream()
                .limit(10)
                .map(o -> new DistributorDashboardResponse.PurchaseSummary(
                        o.getId(),
                        o.getCrop().getCropName(),
                        o.getSeller().getEmail(),
                        o.getOrderState().name(),
                        o.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                ))
                .collect(Collectors.toList());

        // Recent sales
        List<DistributorDashboardResponse.SaleSummary> recentSales = sales.stream()
                .limit(10)
                .map(o -> new DistributorDashboardResponse.SaleSummary(
                        o.getId(),
                        o.getCrop().getCropName(),
                        o.getBuyer().getEmail(),
                        o.getOrderState().name(),
                        o.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                ))
                .collect(Collectors.toList());

        return new DistributorDashboardResponse(email, user.getStatus().name(), stats, recentPurchases, recentSales);
    }

    public RetailerDashboardResponse getRetailerDashboard() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Get inventory (crops currently owned by retailer)
        List<Crop> inventory = cropRepository.findByCurrentOwnerEmail(email);

        // Get orders
        List<Order> purchases = orderRepository.findByBuyer(user);
        List<Order> sales = orderRepository.findBySeller(user);

        long soldItems = sales.stream()
                .filter(o -> o.getOrderState() == OrderState.COMPLETED)
                .count();
        long pendingOrders = sales.stream()
                .filter(o -> o.getOrderState() == OrderState.PLACED || o.getOrderState() == OrderState.ACCEPTED)
                .count();
        double totalRevenue = sales.stream()
                .filter(o -> o.getOrderState() == OrderState.COMPLETED)
                .mapToDouble(o -> 0.0) // Price calculation would go here
                .sum();
        long customersServed = sales.stream()
                .map(o -> o.getBuyer().getId())
                .distinct()
                .count();

        RetailerDashboardResponse.RetailStats stats = new RetailerDashboardResponse.RetailStats(
                inventory.size(), soldItems, pendingOrders, totalRevenue, customersServed
        );

        // Inventory items
        List<RetailerDashboardResponse.InventoryItem> inventoryItems = inventory.stream()
                .limit(20)
                .map(c -> new RetailerDashboardResponse.InventoryItem(
                        c.getId(),
                        c.getCropName(),
                        c.getCurrentOwner().getEmail(),
                        c.getCropState().name(),
                        c.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                        c.getQuantity()
                ))
                .collect(Collectors.toList());

        // Recent customer orders
        List<RetailerDashboardResponse.CustomerOrder> recentOrders = sales.stream()
                .limit(10)
                .map(o -> new RetailerDashboardResponse.CustomerOrder(
                        o.getId(),
                        o.getBuyer().getEmail(),
                        o.getCrop().getCropName(),
                        o.getOrderState().name(),
                        o.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                ))
                .collect(Collectors.toList());

        return new RetailerDashboardResponse(email, user.getStatus().name(), stats, inventoryItems, recentOrders);
    }

    public ConsumerDashboardResponse getConsumerDashboard() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Order> purchases = orderRepository.findByBuyer(user);

        long activeOrders = purchases.stream()
                .filter(o -> o.getOrderState() != OrderState.COMPLETED && o.getOrderState() != OrderState.CANCELLED)
                .count();
        long completedOrders = purchases.stream()
                .filter(o -> o.getOrderState() == OrderState.COMPLETED)
                .count();
        double totalSpent = purchases.stream()
                .filter(o -> o.getOrderState() == OrderState.COMPLETED)
                .mapToDouble(o -> 0.0) // Price calculation would go here
                .sum();

        ConsumerDashboardResponse.ConsumerStats stats = new ConsumerDashboardResponse.ConsumerStats(
                purchases.size(), activeOrders, completedOrders, totalSpent, purchases.size()
        );

        // Purchase history
        List<ConsumerDashboardResponse.PurchaseHistory> purchaseHistory = purchases.stream()
                .limit(20)
                .map(o -> new ConsumerDashboardResponse.PurchaseHistory(
                        o.getId(),
                        o.getCrop().getCropName(),
                        o.getSeller().getEmail(),
                        o.getOrderState().name(),
                        o.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                        o.getCrop().getBlockchainHash()
                ))
                .collect(Collectors.toList());

        // Traceable items
        List<ConsumerDashboardResponse.TraceableItem> traceableItems = purchases.stream()
                .filter(o -> o.getOrderState() == OrderState.COMPLETED)
                .map(o -> new ConsumerDashboardResponse.TraceableItem(
                        o.getCrop().getBlockchainHash(),
                        o.getCrop().getCropName(),
                        o.getCrop().getCropState().name(),
                        o.getCrop().getLocation(),
                        o.getCrop().getHarvestDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                        o.getCrop().getUpdatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
                ))
                .collect(Collectors.toList());

        return new ConsumerDashboardResponse(email, user.getStatus().name(), stats, purchaseHistory, traceableItems);
    }
}
