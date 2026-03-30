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

import java.util.List;

@Service
public class LocationService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private POIService poiService;

    @Autowired
    private UserPoiStateRepository userPoiStateRepository;

    private static final double THRESHOLD = 200.0; // meters

    public String processLocation(LocationRequest request) {

        // Step 1: User check
        User user = userRepository.findById(request.getUserId()).orElse(null);

        if (user == null) {
            user = new User();
            user.setId(request.getUserId());
            user.setConsent(true);
            userRepository.save(user);
        }

        if (!user.isConsent()) {
            return "User has not given consent";
        }

        // Step 2: Fetch POIs
        List<POIResponse> pois = poiService.getNearbyPOIs(
                request.getLatitude(),
                request.getLongitude()
        );

        // Step 3: Check each POI
        for (POIResponse poi : pois) {

            double distance = DistanceUtil.calculateDistance(
                    request.getLatitude(),
                    request.getLongitude(),
                    poi.getLatitude(),
                    poi.getLongitude()
            );

            // Step 4: Inside POI?
            if (distance < THRESHOLD) {

                boolean alreadyEntered =
                        userPoiStateRepository.existsByUserIdAndPoiId(
                                request.getUserId(),
                                poi.getId()
                        );

                // Step 5: Entry detection
                if (!alreadyEntered) {

                    // Save entry
                    UserPoiState state = new UserPoiState();
                    state.setUserId(request.getUserId());
                    state.setPoiId(poi.getId());

                    userPoiStateRepository.save(state);

                    // 🔔 Notification (for now return)
                    return "Welcome to " + poi.getName();
                }
            }
            System.out.println(
                    "POI: " + poi.getName() +
                            " | Distance: " + distance
            );
        }

        return "No new POI entry";
    }
}