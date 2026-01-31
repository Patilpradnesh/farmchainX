package com.farmchainx.backend.service;

import com.farmchainx.backend.entity.Order;
import com.farmchainx.backend.entity.Crop;
import com.farmchainx.backend.entity.User;
import com.farmchainx.backend.enums.OrderState;
import com.farmchainx.backend.enums.CropState;
import com.farmchainx.backend.enums.Role;
import com.farmchainx.backend.repository.OrderRepository;
import com.farmchainx.backend.repository.CropRepository;
import com.farmchainx.backend.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final CropRepository cropRepository;
    private final UserRepository userRepository;
    private final CropService cropService;

    public OrderService(OrderRepository orderRepository, CropRepository cropRepository, UserRepository userRepository, CropService cropService) {
        this.orderRepository = orderRepository;
        this.cropRepository = cropRepository;
        this.userRepository = userRepository;
        this.cropService = cropService;
    }

    @Transactional
    public Order placeOrder(Long cropId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User buyer = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

        Crop crop = cropRepository.findById(cropId).orElseThrow(() -> new RuntimeException("Crop not found"));
        if (crop.getCropState() != CropState.LISTED) {
            throw new RuntimeException("Crop must be LISTED to place an order");
        }

        Order order = new Order();
        order.setCrop(crop);
        order.setBuyer(buyer);
        order.setSeller(crop.getCurrentOwner());
        order.setOrderState(OrderState.PLACED);
        return orderRepository.save(order);
    }

    @Transactional
    public void acceptOrder(Long orderId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User seller = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

        Order order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));
        if (!order.getSeller().equals(seller)) {
            throw new RuntimeException("Only seller can accept order");
        }

        order.transitionTo(OrderState.ACCEPTED, seller);
        orderRepository.save(order);

        // Transition crop to ORDERED
        Crop crop = order.getCrop();
        crop.transitionTo(CropState.ORDERED, seller);
        cropRepository.save(crop);
        cropService.logCropHistory(crop, "STATE_CHANGE", CropState.LISTED, CropState.ORDERED, seller, Role.FARMER);
    }

    @Transactional
    public void shipOrder(Long orderId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User seller = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

        Order order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));
        if (!order.getSeller().equals(seller) || order.getOrderState() != OrderState.ACCEPTED) {
            throw new RuntimeException("Invalid operation");
        }

        order.transitionTo(OrderState.SHIPPED, seller);
        orderRepository.save(order);

        // Transition crop to SHIPPED
        Crop crop = order.getCrop();
        crop.transitionTo(CropState.SHIPPED, seller);
        cropRepository.save(crop);
        cropService.logCropHistory(crop, "STATE_CHANGE", CropState.ORDERED, CropState.SHIPPED, seller, Role.FARMER);
    }

    @Transactional
    public void completeOrder(Long orderId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User buyer = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

        Order order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));
        if (!order.getBuyer().equals(buyer) || order.getOrderState() != OrderState.SHIPPED) {
            throw new RuntimeException("Invalid operation");
        }

        order.transitionTo(OrderState.COMPLETED, buyer);
        orderRepository.save(order);

        // Transfer ownership and transition crop to DELIVERED
        Crop crop = order.getCrop();
        crop.setCurrentOwner(buyer);
        crop.setCurrentOwnerRole(buyer.getRole());
        crop.transitionTo(CropState.DELIVERED, buyer);
        cropRepository.save(crop);
        cropService.logCropHistory(crop, "OWNERSHIP_TRANSFER", CropState.SHIPPED, CropState.DELIVERED, buyer, buyer.getRole());
    }
}
