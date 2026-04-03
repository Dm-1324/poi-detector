package com.example.poi_detector.service;

import com.example.poi_detector.dto.LocationRequest;
import com.example.poi_detector.dto.POIResponse;
import com.example.poi_detector.entity.User;
import com.example.poi_detector.entity.UserPoiState;
import com.example.poi_detector.repository.UserPoiStateRepository;
import com.example.poi_detector.repository.UserRepository;
import com.example.poi_detector.util.DistanceUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class LocationServiceImpl implements LocationService {

    private static final double THRESHOLD = 5.0;
    private static final long COOLDOWN_HOURS = 3;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private POIService poiService;

    @Autowired
    private UserPoiStateRepository userPoiStateRepository;

    @Override
    public String processLocation(LocationRequest request) {

        System.out.println("\n================ LOCATION UPDATE ================");
        System.out.println("User: " + request.getUsername());
        System.out.println("Lat: " + request.getLatitude() + ", Lon: " + request.getLongitude());


        User user = userRepository.findById(request.getUsername()).orElse(null);

        if (user == null) {
            System.out.println("❌ Access Denied: User not found");
            return "User not registered";
        }

        if (!user.isConsent()) {
            System.out.println("❌ User has not given consent");
            return "User has not given consent";
        }


        List<POIResponse> pois;
        try {
            pois = poiService.getNearbyPOIs(
                    request.getLatitude(),
                    request.getLongitude()
            );
        } catch (Exception e) {
            System.out.println("❌ Overpass API failed: " + e.getMessage());
            return "POI service unavailable";
        }

        if (pois == null || pois.isEmpty()) {
            System.out.println("❌ No POIs returned");
            return "No new POI entry";
        }

        System.out.println("✅ POIs fetched: " + pois.size());


        POIResponse nearestPoi = null;
        double minDistance = Double.MAX_VALUE;

        for (POIResponse poi : pois) {

            double distance = DistanceUtil.calculateDistance(
                    request.getLatitude(),
                    request.getLongitude(),
                    poi.getLatitude(),
                    poi.getLongitude()
            );

            System.out.println("➡️ POI: " + poi.getName() + " | Distance: " + distance + " meters");

            if (distance < THRESHOLD && distance < minDistance) {
                minDistance = distance;
                nearestPoi = poi;
            }
        }

        if (nearestPoi == null) {
            System.out.println("❌ No POI within threshold");
            return "No new POI entry";
        }

        System.out.println("🎯 Selected POI: " + nearestPoi.getName());
        System.out.println("📏 Distance: " + minDistance);


        String poiKey = nearestPoi.getUniqueKey();

        UserPoiState existing = userPoiStateRepository.findByUsernameAndPoiId(
                request.getUsername(),
                poiKey
        );


        if (existing == null) {
            System.out.println("🆕 First time entry");

            UserPoiState state = new UserPoiState();
            state.setUsername(request.getUsername());
            state.setPoiId(poiKey);
            state.setPoiName(nearestPoi.getName());
            state.setLastEnteredAt(LocalDateTime.now());
            state.setEntered(true);

            userPoiStateRepository.save(state);

            return "Welcome to " + nearestPoi.getName();
        }


        LocalDateTime lastTime = existing.getLastEnteredAt();
        LocalDateTime now = LocalDateTime.now();

        long minutesPassed = Duration.between(lastTime, now).toMinutes();

        System.out.println("⏱ Last visited: " + lastTime);
        System.out.println("⏱ Minutes passed: " + minutesPassed);

        if (minutesPassed >= (COOLDOWN_HOURS * 60)) {
            System.out.println("✅ Cooldown passed");

            existing.setLastEnteredAt(now);
            existing.setPoiName(nearestPoi.getName());
            existing.setEntered(true);

            userPoiStateRepository.save(existing);

            return "Welcome to " + nearestPoi.getName();
        }

        System.out.println("⛔ Within cooldown → No notification");
        return "No new POI entry";
    }
}