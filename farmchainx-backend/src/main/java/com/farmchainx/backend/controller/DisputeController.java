package com.farmchainx.backend.controller;

import com.farmchainx.backend.common.dto.ApiResponse;
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
    public ResponseEntity<ApiResponse<Dispute>> raiseDispute(@RequestParam Long cropId,
                                                @RequestParam(required = false) Long orderId,
                                                @RequestParam String description,
                                                Principal principal) {
        try {
            String email = principal.getName();
            Long userId = disputeService.getUserIdByEmail(email);
            Dispute dispute = disputeService.raiseDispute(cropId, orderId, userId, description);
            return ResponseEntity.ok(ApiResponse.success("Dispute raised successfully", dispute));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/{id}/resolve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Dispute>> resolveDispute(@PathVariable Long id,
                                                  @RequestParam String resolution,
                                                  @RequestParam(required = false) String adminNotes) {
        try {
            Dispute dispute = disputeService.resolveDispute(id, resolution, adminNotes != null ? adminNotes : "");
            return ResponseEntity.ok(ApiResponse.success("Dispute resolved", dispute));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/{id}/escalate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Dispute>> escalateDispute(@PathVariable Long id,
                                                   @RequestParam String escalationReason) {
        try {
            Dispute dispute = disputeService.escalateDispute(id, escalationReason);
            return ResponseEntity.ok(ApiResponse.success("Dispute escalated", dispute));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/{id}/close")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Dispute>> closeDispute(@PathVariable Long id,
                                                @RequestParam String closureReason) {
        try {
            Dispute dispute = disputeService.closeDispute(id, closureReason);
            return ResponseEntity.ok(ApiResponse.success("Dispute closed", dispute));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Dispute>> updateDispute(@PathVariable Long id,
                                                 @RequestParam(required = false) String description,
                                                 @RequestParam(required = false) String evidence,
                                                 Principal principal) {
        try {
            // Validate user can access this dispute
            if (!disputeService.canUserAccessDispute(id, principal.getName())) {
                return ResponseEntity.status(403).body(ApiResponse.error("Access denied"));
            }

            Dispute dispute = disputeService.updateDispute(id, description, evidence);
            return ResponseEntity.ok(ApiResponse.success("Dispute updated", dispute));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<Dispute>>> getAllDisputes(@RequestParam(required = false) String status) {
        try {
            return ResponseEntity.ok(ApiResponse.success("Disputes retrieved", disputeService.getDisputes(status)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<Dispute>>> getMyDisputes(Principal principal) {
        try {
            return ResponseEntity.ok(ApiResponse.success("My disputes retrieved", disputeService.getMyDisputes(principal.getName())));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/by-crop/{cropId}")
    public ResponseEntity<ApiResponse<List<Dispute>>> getDisputesByCrop(@PathVariable Long cropId) {
        try {
            return ResponseEntity.ok(ApiResponse.success("Disputes by crop retrieved", disputeService.getDisputesByCrop(cropId)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/by-order/{orderId}")
    public ResponseEntity<ApiResponse<List<Dispute>>> getDisputesByOrder(@PathVariable Long orderId) {
        try {
            return ResponseEntity.ok(ApiResponse.success("Disputes by order retrieved", disputeService.getDisputesByOrder(orderId)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/statistics")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getDisputeStatistics() {
        try {
            return ResponseEntity.ok(ApiResponse.success("Dispute statistics retrieved", disputeService.getDisputeStatistics()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/{id}/can-access")
    public ResponseEntity<ApiResponse<Map<String, Boolean>>> canAccessDispute(@PathVariable Long id, Principal principal) {
        try {
            boolean canAccess = disputeService.canUserAccessDispute(id, principal.getName());
            return ResponseEntity.ok(ApiResponse.success("Access check result", Map.of("canAccess", canAccess)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}
