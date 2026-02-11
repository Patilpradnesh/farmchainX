package com.farmchainx.backend.controller;

import com.farmchainx.backend.dto.OrderCreateRequest;
import com.farmchainx.backend.entity.Order;
import com.farmchainx.backend.entity.User;
import com.farmchainx.backend.enums.OrderState;
import com.farmchainx.backend.repository.OrderRepository;
import com.farmchainx.backend.repository.UserRepository;
import com.farmchainx.backend.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderService orderService;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    public OrderController(OrderService orderService, UserRepository userRepository, OrderRepository orderRepository) {
        this.orderService = orderService;
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
    }

    @PostMapping("/create")
    public ResponseEntity<Order> createOrder(@RequestBody OrderCreateRequest request) {
        Order order = orderService.createOrder(
                request.getCropId(),
                request.getRequestedQuantity(),
                request.getOfferedPrice(),
                request.getDeliveryAddress(),
                request.getNotes()
        );
        return ResponseEntity.ok(order);
    }

    @PutMapping("/{orderId}/accept")
    public ResponseEntity<String> acceptOrder(@PathVariable Long orderId) {
        orderService.acceptOrder(orderId);
        return ResponseEntity.ok("Order accepted");
    }

    @PutMapping("/{orderId}/reject")
    public ResponseEntity<String> rejectOrder(@PathVariable Long orderId,
                                             @RequestParam(required = false) String reason) {
        orderService.rejectOrder(orderId, reason != null ? reason : "No reason provided");
        return ResponseEntity.ok("Order rejected");
    }

    @PutMapping("/{orderId}/ship")
    public ResponseEntity<String> shipOrder(@PathVariable Long orderId) {
        orderService.shipOrder(orderId);
        return ResponseEntity.ok("Order shipped");
    }

    @PutMapping("/{orderId}/complete")
    public ResponseEntity<String> completeOrder(@PathVariable Long orderId) {
        orderService.completeOrder(orderId);
        return ResponseEntity.ok("Order completed");
    }

    @PutMapping("/{orderId}/cancel")
    public ResponseEntity<String> cancelOrder(@PathVariable Long orderId) {
        orderService.cancelOrder(orderId);
        return ResponseEntity.ok("Order cancelled");
    }

    @GetMapping("/my")
    public ResponseEntity<List<Order>> getMyOrders() {
        List<Order> orders = orderService.getMyOrders();
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/status/{state}")
    public ResponseEntity<List<Order>> getOrdersByStatus(@PathVariable OrderState state) {
        List<Order> orders = orderService.getOrdersByStatus(state);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{orderId}/validate/{newState}")
    public ResponseEntity<Map<String, Boolean>> validateOrderTransition(
            @PathVariable Long orderId,
            @PathVariable OrderState newState) {
        boolean isValid = orderService.validateOrderTransition(orderId, newState);
        return ResponseEntity.ok(Map.of("valid", isValid));
    }

    @GetMapping("/dashboard/stats")
    public ResponseEntity<Map<String, Object>> getOrderStats() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Order> buyerOrders = orderRepository.findByBuyer(user);
        List<Order> sellerOrders = orderRepository.findBySeller(user);

        long totalBuyerOrders = buyerOrders.size();
        long totalSellerOrders = sellerOrders.size();
        long activeBuyerOrders = buyerOrders.stream()
                .filter(o -> o.getOrderState() != OrderState.COMPLETED && o.getOrderState() != OrderState.CANCELLED)
                .count();
        long activeSellerOrders = sellerOrders.stream()
                .filter(o -> o.getOrderState() != OrderState.COMPLETED && o.getOrderState() != OrderState.CANCELLED)
                .count();

        return ResponseEntity.ok(Map.of(
                "totalBuyerOrders", totalBuyerOrders,
                "totalSellerOrders", totalSellerOrders,
                "activeBuyerOrders", activeBuyerOrders,
                "activeSellerOrders", activeSellerOrders,
                "completedOrders", buyerOrders.stream().filter(o -> o.getOrderState() == OrderState.COMPLETED).count() +
                                  sellerOrders.stream().filter(o -> o.getOrderState() == OrderState.COMPLETED).count()
        ));
    }
}
