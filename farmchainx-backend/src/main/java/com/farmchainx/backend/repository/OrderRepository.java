package com.farmchainx.backend.repository;

import com.farmchainx.backend.entity.Order;
import com.farmchainx.backend.entity.User;
import com.farmchainx.backend.enums.OrderState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByBuyerId(Long buyerId);
    List<Order> findBySellerId(Long sellerId);
    List<Order> findByBuyer(User buyer);
    List<Order> findBySeller(User seller);
    List<Order> findByBuyerOrSeller(User buyer, User seller);
    List<Order> findByBuyerAndOrderStateOrSellerAndOrderState(User buyer1, OrderState state1, User seller, OrderState state2);
    long countByOrderState(OrderState orderState);

    // New: aggregate orders by crop region (crop.location) with counts and total value
    @Query("SELECT c.location AS region, COUNT(o) AS cnt, SUM(COALESCE(o.offeredPrice * o.requestedQuantity, 0)) AS totalValue " +
           "FROM Order o JOIN o.crop c WHERE o.createdAt BETWEEN :start AND :end GROUP BY c.location")
    List<Object[]> findOrderStatsByRegion(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    // New: aggregate orders by buyer role
    @Query("SELECT b.role AS role, COUNT(o) AS cnt, SUM(COALESCE(o.offeredPrice * o.requestedQuantity, 0)) AS totalValue " +
           "FROM Order o JOIN o.buyer b WHERE o.createdAt BETWEEN :start AND :end GROUP BY b.role")
    List<Object[]> findOrderStatsByBuyerRole(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}
