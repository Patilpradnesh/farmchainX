package com.farmchainx.backend.common.exception;

/**
 * Custom exception hierarchy for FarmChainX
 */
public class FarmChainException extends RuntimeException {

    private final String errorCode;

    public FarmChainException(String message) {
        super(message);
        this.errorCode = "FARM_CHAIN_ERROR";
    }

    public FarmChainException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public FarmChainException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "FARM_CHAIN_ERROR";
    }

    public String getErrorCode() {
        return errorCode;
    }
}

// Specific exceptions
class ResourceNotFoundException extends FarmChainException {
    public ResourceNotFoundException(String resource, Long id) {
        super(resource + " not found with id: " + id, "RESOURCE_NOT_FOUND");
    }

    public ResourceNotFoundException(String message) {
        super(message, "RESOURCE_NOT_FOUND");
    }
}

class UnauthorizedAccessException extends FarmChainException {
    public UnauthorizedAccessException(String message) {
        super(message, "UNAUTHORIZED_ACCESS");
    }
}

class InvalidStateTransitionException extends FarmChainException {
    public InvalidStateTransitionException(String from, String to) {
        super("Invalid state transition from " + from + " to " + to, "INVALID_STATE_TRANSITION");
    }
}

class BlockchainIntegrationException extends FarmChainException {
    public BlockchainIntegrationException(String message) {
        super(message, "BLOCKCHAIN_ERROR");
    }

    public BlockchainIntegrationException(String message, Throwable cause) {
        super(message, cause);
    }
}

class PaymentException extends FarmChainException {
    public PaymentException(String message) {
        super(message, "PAYMENT_ERROR");
    }
}

class FileUploadException extends FarmChainException {
    public FileUploadException(String message) {
        super(message, "FILE_UPLOAD_ERROR");
    }
}

class ValidationException extends FarmChainException {
    public ValidationException(String message) {
        super(message, "VALIDATION_ERROR");
    }
}
