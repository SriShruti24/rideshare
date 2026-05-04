package com.rideshare.location_service.service;

import com.rideshare.location_service.dto.DriverLocationRequest;
import com.rideshare.location_service.dto.NearByDriverResponse;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.geo.GeoResults;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.geo.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class LocationService {
     private final RedisTemplate<String,String> redisTemplate;

    //Redis key for all driver locations
    private static final String DRIVERS_GEO_KEY="drivers:location";
/*
 * Update driver location in Redis
 * called every 3 seconds by driver's phone
 * Maps to Redis GeoAdd command
 * **/
    public void updateDriverLocation(DriverLocationRequest driverLocationRequest){
        log.info("Updating location for driver :{}", driverLocationRequest.getDriverId());

        //IMPORTANT : longitude FIRST, latitude SECOND- GeoSpatial Standard
        Point driverPoint =new Point(
                driverLocationRequest.getLongitude(),
                driverLocationRequest.getLatitude()
        );

        //opsForGeo() give access to all Redis Spatial commands in java
        redisTemplate.opsForGeo().add(
                DRIVERS_GEO_KEY,
                driverPoint,
                driverLocationRequest.getDriverId()
        );
        log.info("Location update for driver:{}",driverLocationRequest.getDriverId());
    }
    /*
    * Find nearby drivers within given radius.
    * Called by Matching Service on ride request.
    * Maps to Redis GEO RADIUS  Commands
    * */
    public List<NearByDriverResponse> findNearbyDrivers(
            double latitude, double longitude, double radiusInKm) {

        log.info("Finding drivers near lat: {} long: {} within {}km",
                latitude, longitude, radiusInKm);

        Circle searchArea = new Circle(
                new Point(longitude, latitude),
                new Distance(radiusInKm, Metrics.KILOMETERS)
        );

        GeoResults<RedisGeoCommands.GeoLocation<String>> results =
                redisTemplate.opsForGeo().radius(
                        DRIVERS_GEO_KEY,
                        searchArea,
                        RedisGeoCommands.GeoRadiusCommandArgs.newGeoRadiusArgs()
                                .includeCoordinates()
                                .includeDistance()
                                .sortAscending()
                                .limit(10)
                );

        List<NearByDriverResponse> nearByDrivers = new ArrayList<>();

        if (results != null) {
            results.getContent().forEach(result -> {

                RedisGeoCommands.GeoLocation<String> location = result.getContent();

                nearByDrivers.add(new NearByDriverResponse(
                        location.getName(),
                        location.getPoint().getY(),   // latitude
                        location.getPoint().getX(),   // longitude
                        result.getDistance().getValue()
                ));
            });
        }

        log.info("Found {} drivers nearby", nearByDrivers.size());

        return nearByDrivers;
    }

    /*
     * Remove driver when they go offline
     * Maps to Redis ZREM command
     */
    public void removeDriver(String driverId){
        log.info("Removing driver : {}",driverId);
        redisTemplate.opsForGeo().remove(DRIVERS_GEO_KEY,driverId);
    }


}
