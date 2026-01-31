package com.farmchainx.backend.service;

import com.farmchainx.backend.enums.Role;
import org.springframework.stereotype.Service;
import java.util.Set;

@Service
public class PermissionService {

    public boolean hasPermission(Role role, String action) {
        switch (role) {
            case ADMIN:
                return true; // Full access
            case FARMER:
                return Set.of("REGISTER_CROP", "LIST_CROP", "TRACE_CROP").contains(action);
            case DISTRIBUTOR:
                return Set.of("PLACE_ORDER", "ACCEPT_ORDER", "SHIP_ORDER", "TRACE_CROP").contains(action);
            case RETAILER:
                return Set.of("PLACE_ORDER", "COMPLETE_ORDER", "TRACE_CROP").contains(action);
            case CONSUMER:
                return Set.of("PLACE_ORDER", "COMPLETE_ORDER", "TRACE_CROP").contains(action);
            default:
                return false;
        }
    }
}
