package com.farmchainx.backend.controller;

import com.farmchainx.backend.entity.Dispute;
import com.farmchainx.backend.service.DisputeService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/disputes")
public class DisputeController {
    private final DisputeService disputeService;

    public DisputeController(DisputeService disputeService) {
        this.disputeService = disputeService;
    }

    @PostMapping("/raise")
    public ResponseEntity<Dispute> raiseDispute(@RequestParam Long cropId,
                                                @RequestParam(required = false) Long orderId,
                                                @RequestParam String description,
                                                Principal principal) {
        String email = principal.getName();
        Long userId = disputeService.getUserIdByEmail(email);
        Dispute dispute = disputeService.raiseDispute(cropId, orderId, userId, description);
        return ResponseEntity.ok(dispute);
    }

    @PutMapping("/{id}/resolve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Dispute> resolveDispute(@PathVariable Long id,
                                                  @RequestParam String resolution,
                                                  @RequestParam(required = false) String adminNotes) {
        Dispute dispute = disputeService.resolveDispute(id, resolution, adminNotes != null ? adminNotes : "");
        return ResponseEntity.ok(dispute);
    }

    @PutMapping("/{id}/escalate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Dispute> escalateDispute(@PathVariable Long id,
                                                   @RequestParam String escalationReason) {
        Dispute dispute = disputeService.escalateDispute(id, escalationReason);
        return ResponseEntity.ok(dispute);
    }

    @PutMapping("/{id}/close")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Dispute> closeDispute(@PathVariable Long id,
                                                @RequestParam String closureReason) {
        Dispute dispute = disputeService.closeDispute(id, closureReason);
        return ResponseEntity.ok(dispute);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Dispute> updateDispute(@PathVariable Long id,
                                                 @RequestParam(required = false) String description,
                                                 @RequestParam(required = false) String evidence,
                                                 Principal principal) {
        // Validate user can access this dispute
        if (!disputeService.canUserAccessDispute(id, principal.getName())) {
            return ResponseEntity.status(403).build();
        }

        Dispute dispute = disputeService.updateDispute(id, description, evidence);
        return ResponseEntity.ok(dispute);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Dispute>> getAllDisputes(@RequestParam(required = false) String status) {
        return ResponseEntity.ok(disputeService.getDisputes(status));
    }

    @GetMapping("/my")
    public ResponseEntity<List<Dispute>> getMyDisputes(Principal principal) {
        return ResponseEntity.ok(disputeService.getMyDisputes(principal.getName()));
    }

    @GetMapping("/by-crop/{cropId}")
    public ResponseEntity<List<Dispute>> getDisputesByCrop(@PathVariable Long cropId) {
        return ResponseEntity.ok(disputeService.getDisputesByCrop(cropId));
    }

    @GetMapping("/by-order/{orderId}")
    public ResponseEntity<List<Dispute>> getDisputesByOrder(@PathVariable Long orderId) {
        return ResponseEntity.ok(disputeService.getDisputesByOrder(orderId));
    }

    @GetMapping("/statistics")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Long>> getDisputeStatistics() {
        return ResponseEntity.ok(disputeService.getDisputeStatistics());
    }

    @GetMapping("/{id}/can-access")
    public ResponseEntity<Map<String, Boolean>> canAccessDispute(@PathVariable Long id, Principal principal) {
        boolean canAccess = disputeService.canUserAccessDispute(id, principal.getName());
        return ResponseEntity.ok(Map.of("canAccess", canAccess));
    }
}
