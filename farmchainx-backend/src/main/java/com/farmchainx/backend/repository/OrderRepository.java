package com.farmchainx.backend.repository;

import com.farmchainx.backend.entity.Order;
import com.farmchainx.backend.entity.User;
import com.farmchainx.backend.enums.OrderState;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByBuyerId(Long buyerId);
    List<Order> findBySellerId(Long sellerId);
    List<Order> findByBuyer(User buyer);
    List<Order> findBySeller(User seller);
    List<Order> findByBuyerOrSeller(User buyer, User seller);
    List<Order> findByBuyerAndOrderStateOrSellerAndOrderState(User buyer1, OrderState state1, User seller, OrderState state2);
    long countByOrderState(OrderState orderState);
}
