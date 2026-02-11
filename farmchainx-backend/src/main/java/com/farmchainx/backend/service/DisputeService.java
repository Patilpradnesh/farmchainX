package com.farmchainx.backend.service;

import com.farmchainx.backend.entity.Crop;
import com.farmchainx.backend.entity.Dispute;
import com.farmchainx.backend.entity.Order;
import com.farmchainx.backend.entity.User;
import com.farmchainx.backend.repository.CropRepository;
import com.farmchainx.backend.repository.DisputeRepository;
import com.farmchainx.backend.repository.OrderRepository;
import com.farmchainx.backend.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DisputeService {
    private final DisputeRepository disputeRepository;
    private final CropRepository cropRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final AuditService auditService;

    public DisputeService(DisputeRepository disputeRepository, CropRepository cropRepository, OrderRepository orderRepository, UserRepository userRepository, AuditService auditService) {
        this.disputeRepository = disputeRepository;
        this.cropRepository = cropRepository;
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.auditService = auditService;
    }

    @Transactional
    public Dispute raiseDispute(Long cropId, Long orderId, Long userId, String description) {
        Crop crop = cropRepository.findById(cropId).orElseThrow(() -> new RuntimeException("Crop not found"));
        Order order = orderId != null ? orderRepository.findById(orderId).orElse(null) : null;
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        Dispute dispute = new Dispute();
        dispute.setCrop(crop);
        dispute.setOrder(order);
        dispute.setRaisedBy(user);
        dispute.setDescription(description);
        dispute.setStatus("OPEN");
        Dispute saved = disputeRepository.save(dispute);
        auditService.logAction("DISPUTE_RAISED", user, "Dispute ID: " + saved.getId() + ", Crop ID: " + cropId + ", Order ID: " + orderId);
        return saved;
    }

    @Transactional
    public Dispute resolveDispute(Long disputeId, String resolution, String adminNotes) {
        Dispute dispute = disputeRepository.findById(disputeId).orElseThrow(() -> new RuntimeException("Dispute not found"));

        if (!"OPEN".equals(dispute.getStatus())) {
            throw new RuntimeException("Dispute is already resolved or closed");
        }

        dispute.setStatus("RESOLVED");
        dispute.setResolvedAt(LocalDateTime.now());
        dispute.setResolution(resolution);
        dispute.setAdminNotes(adminNotes);

        Dispute saved = disputeRepository.save(dispute);
        auditService.logAction("DISPUTE_RESOLVED", dispute.getRaisedBy(), "Dispute ID: " + disputeId + ", Resolution: " + resolution);
        return saved;
    }

    @Transactional
    public Dispute escalateDispute(Long disputeId, String escalationReason) {
        Dispute dispute = disputeRepository.findById(disputeId).orElseThrow(() -> new RuntimeException("Dispute not found"));

        dispute.setStatus("ESCALATED");
        dispute.setEscalatedAt(LocalDateTime.now());
        dispute.setEscalationReason(escalationReason);

        Dispute saved = disputeRepository.save(dispute);
        auditService.logAction("DISPUTE_ESCALATED", dispute.getRaisedBy(), "Dispute ID: " + disputeId);
        return saved;
    }

    @Transactional
    public Dispute closeDispute(Long disputeId, String closureReason) {
        Dispute dispute = disputeRepository.findById(disputeId).orElseThrow(() -> new RuntimeException("Dispute not found"));

        dispute.setStatus("CLOSED");
        dispute.setClosedAt(LocalDateTime.now());
        dispute.setClosureReason(closureReason);

        Dispute saved = disputeRepository.save(dispute);
        auditService.logAction("DISPUTE_CLOSED", dispute.getRaisedBy(), "Dispute ID: " + disputeId);
        return saved;
    }

    public List<Dispute> getDisputes(String status) {
        if (status != null) {
            return disputeRepository.findByStatus(status);
        }
        return disputeRepository.findAll();
    }

    public List<Dispute> getMyDisputes(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        return disputeRepository.findByRaisedBy(user);
    }

    public List<Dispute> getDisputesByCrop(Long cropId) {
        return disputeRepository.findByCropId(cropId);
    }

    public List<Dispute> getDisputesByOrder(Long orderId) {
        return disputeRepository.findByOrderId(orderId);
    }

    public Dispute updateDispute(Long disputeId, String newDescription, String evidence) {
        Dispute dispute = disputeRepository.findById(disputeId).orElseThrow(() -> new RuntimeException("Dispute not found"));

        if (!"OPEN".equals(dispute.getStatus())) {
            throw new RuntimeException("Cannot update resolved or closed dispute");
        }

        if (newDescription != null) {
            dispute.setDescription(newDescription);
        }
        if (evidence != null) {
            dispute.setEvidence(evidence);
        }
        dispute.setUpdatedAt(LocalDateTime.now());

        return disputeRepository.save(dispute);
    }

    public boolean canUserAccessDispute(Long disputeId, String userEmail) {
        Dispute dispute = disputeRepository.findById(disputeId).orElse(null);
        if (dispute == null) return false;

        User user = userRepository.findByEmail(userEmail).orElse(null);
        if (user == null) return false;

        // User can access if they raised the dispute or are involved in the crop/order
        return dispute.getRaisedBy().equals(user) ||
               dispute.getCrop().getCurrentOwner().equals(user) ||
               (dispute.getOrder() != null &&
                (dispute.getOrder().getBuyer().equals(user) || dispute.getOrder().getSeller().equals(user)));
    }

    public Long getUserIdByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(com.farmchainx.backend.entity.User::getId)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));
    }

    public Map<String, Long> getDisputeStatistics() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("total", disputeRepository.count());
        stats.put("open", disputeRepository.countByStatus("OPEN"));
        stats.put("resolved", disputeRepository.countByStatus("RESOLVED"));
        stats.put("escalated", disputeRepository.countByStatus("ESCALATED"));
        stats.put("closed", disputeRepository.countByStatus("CLOSED"));
        return stats;
    }
}
