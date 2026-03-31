package com.example.poi_detector.service;

import com.example.poi_detector.dto.POIResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class POIService {

    @Autowired
    private RestTemplate restTemplate;

    private static final String OVERPASS_URL = "https://overpass-api.de/api/interpreter";

    public List<POIResponse> getNearbyPOIs(double lat, double lon) {

        try {
            System.out.println("📡 Calling Overpass API...");
            System.out.println("📡 Calling Overpass API...");

            String query = buildQuery(lat, lon);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.TEXT_PLAIN);

            HttpEntity<String> entity = new HttpEntity<>(query, headers);

            Map response = restTemplate.postForObject(
                    OVERPASS_URL,
                    entity,
                    Map.class
            );

            System.out.println("✅ Overpass response received");

            return parseResponse(response);

        } catch (Exception e) {
            System.out.println("❌ Overpass API FAILED: " + e.getMessage());

            // IMPORTANT: Don't break app
            return new ArrayList<>();
        }
    }

    private String buildQuery(double lat, double lon) {

        return "[out:json];(" +
                "node[\"amenity\"=\"restaurant\"](around:500," + lat + "," + lon + ");" +
                "node[\"amenity\"=\"fuel\"](around:500," + lat + "," + lon + ");" +
                "node[\"shop\"=\"mall\"](around:500," + lat + "," + lon + ");" +
                ");out;";
    }

    private List<POIResponse> parseResponse(Map response) {

        List<POIResponse> pois = new ArrayList<>();

        if (response == null || response.get("elements") == null) {
            return pois; // empty list
        }

        List<Map> elements = (List<Map>) response.get("elements");

        for (Map element : elements) {

            POIResponse poi = new POIResponse();

            poi.setId(String.valueOf(element.get("id")));
            poi.setLatitude((Double) element.get("lat"));
            poi.setLongitude((Double) element.get("lon"));

            Map tags = (Map) element.get("tags");

            if (tags != null && tags.get("name") != null) {
                poi.setName((String) tags.get("name"));
            } else {
                poi.setName("Unknown POI");
            }

            pois.add(poi);
        }

        return pois;
    }
}