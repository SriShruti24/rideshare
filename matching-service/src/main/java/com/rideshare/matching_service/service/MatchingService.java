package com.rideshare.matching_service.service;

import com.rideshare.matching_service.client.LocationServiceClient;
import com.rideshare.matching_service.dto.NearByDriverResponse;
import com.rideshare.matching_service.event.RideMatchedEvent;
import com.rideshare.matching_service.event.RideRequestedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.util.Comparator;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class MatchingService {

    private final LocationServiceClient locationServiceClient;
    private final KafkaTemplate<String, RideMatchedEvent> kafkaTemplate;

    private static final String RIDE_MATCHED_TOPIC = "ride.matched";
    private static final double DEFAULT_SEARCH_RADIUS_KM = 5.0;

    public void matchDriverForRide(RideRequestedEvent event) {

        log.info("Matching driver for ride: {}", event.getRideId());

        // STEP 1: Get nearby drivers
        List<NearByDriverResponse> drivers =
                locationServiceClient.getNearbyDrivers(
                        event.getPickupLatitude(),
                        event.getPickupLongitude(),
                        DEFAULT_SEARCH_RADIUS_KM
                );

        if (drivers == null || drivers.isEmpty()) {
            log.warn("No drivers found for ride: {}", event.getRideId());
            return;
        }

        // STEP 2: Score and pick best driver
        Optional<NearByDriverResponse> bestDriver = findBestDriver(drivers);

        if (bestDriver.isEmpty()) {
            log.warn("Could not find suitable driver for ride");
            return;
        }

        NearByDriverResponse assignedDriver = bestDriver.get();

        // STEP 3: Publish event to Kafka
        RideMatchedEvent matchedEvent = new RideMatchedEvent(
                event.getRideId(),
                event.getRiderId(),
                assignedDriver.getDriverId(),
                assignedDriver.getLatitude(),
                assignedDriver.getLongitude(),
                assignedDriver.getDistanceInKm()
        );

        kafkaTemplate.send(RIDE_MATCHED_TOPIC, event.getRideId(), matchedEvent);

        log.info("RideMatchedEvent published");
    }
    /*
    * Driver Scoring algorithm
    *
    * Distance: 70%
    * Rating:30%
    *
    * Score=(less distance more score)=(1/distance)*distanceWeight+ rating + ratingWeight
    * @param drivers
    * */
    private Optional<NearByDriverResponse> findBestDriver(
            List<NearByDriverResponse> drivers) {

        double distanceWeight = 0.7;
        double ratingWeight = 0.3;

        return drivers.stream()
                .max(Comparator.comparingDouble(driver -> {

                    // Distance score: closer = higher score
                    // Add 0.1 to avoid division by zero
                    double distanceScore = 1.0 / (driver.getDistanceInKm() + 0.1);

                    // Simulated rating between 4.0 and 5.0
                    // In production, fetch from driver service
                    double simulatedRating = 4.0 + Math.random();

                    // Final score
                    return (distanceScore * distanceWeight)
                            + (simulatedRating * ratingWeight);
                }));
    }
}