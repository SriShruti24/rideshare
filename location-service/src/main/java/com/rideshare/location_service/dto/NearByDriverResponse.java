package com.rideshare.location_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NearByDriverResponse {
    private String driverId;
    private double latitude;
    private double longitude;
    private double distanceInKm;
}
