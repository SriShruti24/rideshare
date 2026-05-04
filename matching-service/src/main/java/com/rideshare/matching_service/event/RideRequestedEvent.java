package com.rideshare.matching_service.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
/*
 * Event consumed from Kafka topic: ride.requested
 * Published by Ride Service when a rider request a ride
 *
 *
 * */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RideRequestedEvent {
    private String riderId;
    private String rideId;
    private double pickupLatitude;
    private double pickupLongitude;
    private String dropLatitude;
    private double dropLongitude;
    private String dropAddress;
}
