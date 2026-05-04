package com.rideshare.ride_service.dto;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import com.rideshare.ride_service.model.RideStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

// when user request what response he/she get
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RideResponse {

    private String id;
    private String riderId;
    private String driverId;

    private double pickupLatitude;
    private double pickupLongitude;
    private String pickupAddress;

    private double dropLatitude;
    private double dropLongitude;
    private String dropAddress;

    //Ride status -tracks the lifecycle
    private RideStatus status;

    private double estimatedFare;
    private double actualFare;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
}
