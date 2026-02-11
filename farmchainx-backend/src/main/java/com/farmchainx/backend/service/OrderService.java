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

import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final CropRepository cropRepository;
    private final UserRepository userRepository;
    private final CropService cropService;
    private final AuditService auditService;
    private final NotificationService notificationService;

    public OrderService(OrderRepository orderRepository, CropRepository cropRepository,
                       UserRepository userRepository, CropService cropService,
                       AuditService auditService, NotificationService notificationService) {
        this.orderRepository = orderRepository;
        this.cropRepository = cropRepository;
        this.userRepository = userRepository;
        this.cropService = cropService;
        this.auditService = auditService;
        this.notificationService = notificationService;
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
        Order savedOrder = orderRepository.save(order);
        auditService.logAction("ORDER_PLACED", buyer, "Order ID: " + savedOrder.getId() + ", Crop ID: " + cropId);

        // Send notifications
        notificationService.sendOrderNotification(buyer, "placed", savedOrder.getId().toString(), crop.getCropName());
        notificationService.sendOrderNotification(crop.getCurrentOwner(), "received", savedOrder.getId().toString(), crop.getCropName());

        return savedOrder;
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
        auditService.logAction("ORDER_ACCEPTED", seller, "Order ID: " + orderId);

        // Send notifications
        notificationService.sendOrderNotification(order.getBuyer(), "accepted", orderId.toString(), order.getCrop().getCropName());
        notificationService.sendOrderNotification(seller, "accepted by you", orderId.toString(), order.getCrop().getCropName());

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
        auditService.logAction("ORDER_SHIPPED", seller, "Order ID: " + orderId);

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
        auditService.logAction("ORDER_COMPLETED", buyer, "Order ID: " + orderId);

        // Transfer ownership and transition crop to DELIVERED
        Crop crop = order.getCrop();
        crop.setCurrentOwner(buyer);
        crop.setCurrentOwnerRole(buyer.getRole());
        crop.transitionTo(CropState.DELIVERED, buyer);
        cropRepository.save(crop);
        cropService.logCropHistory(crop, "OWNERSHIP_TRANSFER", CropState.SHIPPED, CropState.DELIVERED, buyer, buyer.getRole());
    }

    @Transactional
    public Order createOrder(Long cropId, Double requestedQuantity, Double offeredPrice, String deliveryAddress, String notes) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User buyer = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

        Crop crop = cropRepository.findById(cropId).orElseThrow(() -> new RuntimeException("Crop not found"));

        // Validation
        if (crop.getCropState() != CropState.LISTED) {
            throw new RuntimeException("Crop must be LISTED to place an order");
        }

        if (crop.getCurrentOwner().equals(buyer)) {
            throw new RuntimeException("Cannot order your own crop");
        }

        if (requestedQuantity != null && requestedQuantity > crop.getQuantity()) {
            throw new RuntimeException("Requested quantity exceeds available quantity");
        }

        Order order = new Order();
        order.setCrop(crop);
        order.setBuyer(buyer);
        order.setSeller(crop.getCurrentOwner());
        order.setOrderState(OrderState.PLACED);
        order.setRequestedQuantity(requestedQuantity);
        order.setOfferedPrice(offeredPrice);
        order.setDeliveryAddress(deliveryAddress);
        order.setNotes(notes);

        Order savedOrder = orderRepository.save(order);
        auditService.logAction("ORDER_CREATED", buyer, "Order ID: " + savedOrder.getId() + ", Crop ID: " + cropId);
        return savedOrder;
    }

    @Transactional
    public void cancelOrder(Long orderId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

        Order order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));

        // Validation - only buyer can cancel before acceptance, both can cancel after
        boolean canCancel = false;
        if (order.getOrderState() == OrderState.PLACED && order.getBuyer().equals(user)) {
            canCancel = true;
        } else if (order.getOrderState() == OrderState.ACCEPTED &&
                  (order.getBuyer().equals(user) || order.getSeller().equals(user))) {
            canCancel = true;
        }

        if (!canCancel) {
            throw new RuntimeException("Cannot cancel order in current state");
        }

        order.transitionTo(OrderState.CANCELLED, user);
        orderRepository.save(order);
        auditService.logAction("ORDER_CANCELLED", user, "Order ID: " + orderId);

        // If crop was in ORDERED state, revert to LISTED
        Crop crop = order.getCrop();
        if (crop.getCropState() == CropState.ORDERED) {
            crop.transitionTo(CropState.LISTED, user);
            cropRepository.save(crop);
            cropService.logCropHistory(crop, "STATE_CHANGE", CropState.ORDERED, CropState.LISTED, user, user.getRole());
        }
    }

    public List<Order> getMyOrders() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

        return orderRepository.findByBuyerOrSeller(user, user);
    }

    public List<Order> getOrdersByStatus(OrderState state) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

        return orderRepository.findByBuyerAndOrderStateOrSellerAndOrderState(user, state, user, state);
    }

    @Transactional
    public void rejectOrder(Long orderId, String reason) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User seller = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

        Order order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));

        if (!order.getSeller().equals(seller) || order.getOrderState() != OrderState.PLACED) {
            throw new RuntimeException("Cannot reject order");
        }

        order.setRejectionReason(reason);
        order.transitionTo(OrderState.CANCELLED, seller);
        orderRepository.save(order);
        auditService.logAction("ORDER_REJECTED", seller, "Order ID: " + orderId + ", Reason: " + reason);
    }

    public boolean validateOrderTransition(Long orderId, OrderState newState) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));
        OrderState currentState = order.getOrderState();

        return switch (newState) {
            case PLACED -> false; // Cannot transition back to placed
            case ACCEPTED -> currentState == OrderState.PLACED;
            case SHIPPED -> currentState == OrderState.ACCEPTED;
            case COMPLETED -> currentState == OrderState.SHIPPED;
            case CANCELLED -> currentState == OrderState.PLACED || currentState == OrderState.ACCEPTED;
        };
    }
}
