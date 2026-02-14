package com.farmchainx.backend.controller;

import com.farmchainx.backend.common.dto.ApiResponse;
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
    public ResponseEntity<ApiResponse<Order>> createOrder(@RequestBody OrderCreateRequest request) {
        try {
            Order order = orderService.createOrder(
                    request.getCropId(),
                    request.getRequestedQuantity(),
                    request.getOfferedPrice(),
                    request.getDeliveryAddress(),
                    request.getNotes()
            );
            return ResponseEntity.ok(ApiResponse.success("Order created successfully", order));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/{orderId}/accept")
    public ResponseEntity<ApiResponse<String>> acceptOrder(@PathVariable Long orderId) {
        try {
            orderService.acceptOrder(orderId);
            return ResponseEntity.ok(ApiResponse.success("Order accepted", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/{orderId}/reject")
    public ResponseEntity<ApiResponse<String>> rejectOrder(@PathVariable Long orderId,
                                             @RequestParam(required = false) String reason) {
        try {
            orderService.rejectOrder(orderId, reason != null ? reason : "No reason provided");
            return ResponseEntity.ok(ApiResponse.success("Order rejected", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/{orderId}/ship")
    public ResponseEntity<ApiResponse<String>> shipOrder(@PathVariable Long orderId) {
        try {
            orderService.shipOrder(orderId);
            return ResponseEntity.ok(ApiResponse.success("Order shipped", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/{orderId}/complete")
    public ResponseEntity<ApiResponse<String>> completeOrder(@PathVariable Long orderId) {
        try {
            orderService.completeOrder(orderId);
            return ResponseEntity.ok(ApiResponse.success("Order completed", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/{orderId}/cancel")
    public ResponseEntity<ApiResponse<String>> cancelOrder(@PathVariable Long orderId) {
        try {
            orderService.cancelOrder(orderId);
            return ResponseEntity.ok(ApiResponse.success("Order cancelled", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<Order>>> getMyOrders() {
        try {
            List<Order> orders = orderService.getMyOrders();
            return ResponseEntity.ok(ApiResponse.success("Orders retrieved", orders));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/status/{state}")
    public ResponseEntity<ApiResponse<List<Order>>> getOrdersByStatus(@PathVariable OrderState state) {
        try {
            List<Order> orders = orderService.getOrdersByStatus(state);
            return ResponseEntity.ok(ApiResponse.success("Orders retrieved", orders));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/{orderId}/validate/{newState}")
    public ResponseEntity<ApiResponse<Map<String, Boolean>>> validateOrderTransition(
            @PathVariable Long orderId,
            @PathVariable OrderState newState) {
        try {
            boolean isValid = orderService.validateOrderTransition(orderId, newState);
            return ResponseEntity.ok(ApiResponse.success("Validation result", Map.of("valid", isValid)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/dashboard/stats")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getOrderStats() {
        try {
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

            Map<String, Object> stats = Map.of(
                    "totalBuyerOrders", totalBuyerOrders,
                    "totalSellerOrders", totalSellerOrders,
                    "activeBuyerOrders", activeBuyerOrders,
                    "activeSellerOrders", activeSellerOrders,
                    "completedOrders", buyerOrders.stream().filter(o -> o.getOrderState() == OrderState.COMPLETED).count() +
                                      sellerOrders.stream().filter(o -> o.getOrderState() == OrderState.COMPLETED).count()
            );
            return ResponseEntity.ok(ApiResponse.success("Order stats retrieved", stats));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}
