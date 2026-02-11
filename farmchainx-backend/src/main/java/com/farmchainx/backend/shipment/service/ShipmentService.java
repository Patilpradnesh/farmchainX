package com.farmchainx.backend.shipment.service;

import com.farmchainx.backend.entity.Order;
import com.farmchainx.backend.entity.User;
import com.farmchainx.backend.repository.OrderRepository;
import com.farmchainx.backend.repository.UserRepository;
import com.farmchainx.backend.shipment.dto.ShipmentCreateRequest;
import com.farmchainx.backend.shipment.dto.ShipmentResponse;
import com.farmchainx.backend.shipment.entity.Shipment;
import com.farmchainx.backend.shipment.entity.ShipmentTrackingEvent;
import com.farmchainx.backend.shipment.enums.ShipmentStatus;
import com.farmchainx.backend.shipment.repository.ShipmentRepository;
import com.farmchainx.backend.shipment.repository.ShipmentTrackingEventRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ShipmentService {

    private final ShipmentRepository shipmentRepository;
    private final ShipmentTrackingEventRepository trackingEventRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    public ShipmentService(ShipmentRepository shipmentRepository,
                          ShipmentTrackingEventRepository trackingEventRepository,
                          OrderRepository orderRepository,
                          UserRepository userRepository) {
        this.shipmentRepository = shipmentRepository;
        this.trackingEventRepository = trackingEventRepository;
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public ShipmentResponse createShipment(ShipmentCreateRequest request) {
        // Validate order exists
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // Check if shipment already exists for this order
        if (shipmentRepository.findByOrderId(request.getOrderId()).isPresent()) {
            throw new RuntimeException("Shipment already exists for this order");
        }

        // Get current user
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Create shipment
        Shipment shipment = new Shipment();
        shipment.setOrder(order);
        shipment.setCarrier(currentUser);
        shipment.setVehicleNumber(request.getVehicleNumber());
        shipment.setDriverName(request.getDriverName());
        shipment.setDriverContact(request.getDriverContact());
        shipment.setDestinationAddress(request.getDestinationAddress());
        shipment.setDestinationLatitude(request.getDestinationLatitude());
        shipment.setDestinationLongitude(request.getDestinationLongitude());
        shipment.setEstimatedDeliveryAt(request.getEstimatedDeliveryAt());
        shipment.setNotes(request.getNotes());
        shipment.setStatus(ShipmentStatus.CREATED);

        shipment = shipmentRepository.save(shipment);

        // Create initial tracking event
        createTrackingEvent(shipment, "SHIPMENT_CREATED",
                "Shipment created and ready for dispatch", currentUser);

        return toResponse(shipment);
    }

    @Transactional
    public ShipmentResponse dispatchShipment(Long shipmentId) {
        Shipment shipment = getShipmentById(shipmentId);

        if (shipment.getStatus() != ShipmentStatus.CREATED) {
            throw new RuntimeException("Only shipments in CREATED status can be dispatched");
        }

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        shipment.dispatch();
        shipment = shipmentRepository.save(shipment);

        createTrackingEvent(shipment, "STATUS_CHANGE",
                "Shipment dispatched and in transit", currentUser);

        return toResponse(shipment);
    }

    @Transactional
    public ShipmentResponse updateLocation(Long shipmentId, Double latitude,
                                          Double longitude, String locationName) {
        Shipment shipment = getShipmentById(shipmentId);

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        shipment.updateLocation(latitude, longitude, locationName);
        shipment = shipmentRepository.save(shipment);

        // Create location tracking event
        ShipmentTrackingEvent event = new ShipmentTrackingEvent();
        event.setShipment(shipment);
        event.setEventType("LOCATION_UPDATE");
        event.setDescription("Location updated: " + locationName);
        event.setLatitude(latitude);
        event.setLongitude(longitude);
        event.setLocationName(locationName);
        event.setRecordedBy(currentUser);
        trackingEventRepository.save(event);

        return toResponse(shipment);
    }

    @Transactional
    public ShipmentResponse updateCondition(Long shipmentId, Double temperature, Double humidity) {
        Shipment shipment = getShipmentById(shipmentId);

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        shipment.updateCondition(temperature, humidity);
        shipment = shipmentRepository.save(shipment);

        // Create condition tracking event
        ShipmentTrackingEvent event = new ShipmentTrackingEvent();
        event.setShipment(shipment);
        event.setEventType("CONDITION_UPDATE");
        event.setDescription(String.format("Condition: Temp %.1f°C, Humidity %.1f%%",
                temperature, humidity));
        event.setTemperature(temperature);
        event.setHumidity(humidity);
        event.setRecordedBy(currentUser);
        trackingEventRepository.save(event);

        // Check for alerts
        if ("CRITICAL".equals(shipment.getConditionStatus())) {
            createTrackingEvent(shipment, "CONDITION_ALERT",
                    "CRITICAL: Temperature out of safe range!", currentUser);
        }

        return toResponse(shipment);
    }

    @Transactional
    public ShipmentResponse markDelivered(Long shipmentId) {
        Shipment shipment = getShipmentById(shipmentId);

        if (shipment.getStatus() != ShipmentStatus.IN_TRANSIT &&
            shipment.getStatus() != ShipmentStatus.OUT_FOR_DELIVERY) {
            throw new RuntimeException("Only shipments in transit can be marked as delivered");
        }

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        shipment.markDelivered();
        shipment = shipmentRepository.save(shipment);

        createTrackingEvent(shipment, "STATUS_CHANGE",
                "Shipment delivered successfully", currentUser);

        return toResponse(shipment);
    }

    public ShipmentResponse getShipmentByTrackingNumber(String trackingNumber) {
        Shipment shipment = shipmentRepository.findByTrackingNumber(trackingNumber)
                .orElseThrow(() -> new RuntimeException("Shipment not found"));
        return toResponse(shipment);
    }

    public ShipmentResponse getShipmentByOrderId(Long orderId) {
        Shipment shipment = shipmentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Shipment not found for this order"));
        return toResponse(shipment);
    }

    public List<ShipmentResponse> getActiveShipments() {
        List<ShipmentStatus> activeStatuses = List.of(
                ShipmentStatus.CREATED,
                ShipmentStatus.IN_TRANSIT,
                ShipmentStatus.OUT_FOR_DELIVERY
        );
        return shipmentRepository.findByStatusIn(activeStatuses).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private Shipment getShipmentById(Long id) {
        return shipmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Shipment not found"));
    }

    private void createTrackingEvent(Shipment shipment, String eventType,
                                    String description, User user) {
        ShipmentTrackingEvent event = new ShipmentTrackingEvent();
        event.setShipment(shipment);
        event.setEventType(eventType);
        event.setDescription(description);
        event.setRecordedBy(user);
        trackingEventRepository.save(event);
    }

    private ShipmentResponse toResponse(Shipment shipment) {
        ShipmentResponse response = new ShipmentResponse();
        response.setId(shipment.getId());
        response.setTrackingNumber(shipment.getTrackingNumber());
        response.setStatus(shipment.getStatus());

        // Order info
        response.setOrderId(shipment.getOrder().getId());
        if (shipment.getOrder().getCrop() != null) {
            response.setCropName(shipment.getOrder().getCrop().getCropName());
            response.setQuantity(shipment.getOrder().getRequestedQuantity());
        }

        // Driver info
        response.setVehicleNumber(shipment.getVehicleNumber());
        response.setDriverName(shipment.getDriverName());
        response.setDriverContact(shipment.getDriverContact());

        // Location
        if (shipment.getCurrentLatitude() != null) {
            response.setCurrentLocation(new ShipmentResponse.LocationInfo(
                    shipment.getCurrentLatitude(),
                    shipment.getCurrentLongitude(),
                    shipment.getCurrentLocation(),
                    shipment.getUpdatedAt()
            ));
        }

        response.setDestination(new ShipmentResponse.LocationInfo(
                shipment.getDestinationLatitude(),
                shipment.getDestinationLongitude(),
                shipment.getDestinationAddress(),
                null
        ));

        // Condition
        response.setCurrentTemperature(shipment.getCurrentTemperature());
        response.setCurrentHumidity(shipment.getCurrentHumidity());
        response.setConditionStatus(shipment.getConditionStatus());

        // Timeline
        response.setCreatedAt(shipment.getCreatedAt());
        response.setDispatchedAt(shipment.getDispatchedAt());
        response.setEstimatedDeliveryAt(shipment.getEstimatedDeliveryAt());
        response.setActualDeliveryAt(shipment.getActualDeliveryAt());

        // Tracking events
        List<ShipmentTrackingEvent> events = trackingEventRepository
                .findByShipmentIdOrderByTimestampDesc(shipment.getId());
        response.setTrackingEvents(events.stream()
                .map(e -> new ShipmentResponse.TrackingEventInfo(
                        e.getEventType(),
                        e.getDescription(),
                        e.getLatitude(),
                        e.getLongitude(),
                        e.getLocationName(),
                        e.getTimestamp()
                ))
                .collect(Collectors.toList()));

        return response;
    }
}
