package com.example.poi_detector.service;

import com.example.poi_detector.dto.POIResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class POIServiceImpl implements POIService {

    private static final String OVERPASS_URL = "https://overpass-api.de/api/interpreter";

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public List<POIResponse> getNearbyPOIs(double lat, double lon) {

        try {
            System.out.println("📡 Calling Overpass API...");
            System.out.println("Lat: " + lat + ", Lon: " + lon);

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

        } catch (ResourceAccessException | HttpStatusCodeException e) {
            System.out.println("❌ Overpass Connection/Timeout Error: " + e.getMessage());
            throw new RuntimeException("Overpass server not responding, please try again");
        } catch (Exception e) {
            System.out.println("❌ Overpass General Failure: " + e.getMessage());
            throw new RuntimeException("Overpass server not responding, please try again");
        }
    }

    private String buildQuery(double lat, double lon) {
        return "[out:json];(" +
                "node[\"amenity\"~\"restaurant|fuel\"](around:150," + lat + "," + lon + ");" +
                "way[\"amenity\"~\"restaurant|fuel\"](around:150," + lat + "," + lon + ");" +
                "node[\"shop\"=\"mall\"](around:150," + lat + "," + lon + ");" +
                "way[\"shop\"=\"mall\"](around:150," + lat + "," + lon + ");" +
                "relation[\"shop\"=\"mall\"](around:150," + lat + "," + lon + ");" +
                ");out center;";
    }

    private List<POIResponse> parseResponse(Map response) {
        List<POIResponse> pois = new ArrayList<>();
        if (response == null || response.get("elements") == null) return pois;

        List<Map> elements = (List<Map>) response.get("elements");

        for (Map element : elements) {
            POIResponse poi = new POIResponse();
            poi.setId(String.valueOf(element.get("id")));

            if (element.containsKey("lat")) {
                poi.setLatitude((Double) element.get("lat"));
                poi.setLongitude((Double) element.get("lon"));
            } else if (element.containsKey("center")) {
                Map center = (Map) element.get("center");
                poi.setLatitude((Double) center.get("lat"));
                poi.setLongitude((Double) center.get("lon"));
            }

            Map tags = (Map) element.get("tags");
            poi.setName(tags != null && tags.get("name") != null ? (String) tags.get("name") : "Unknown POI");

            pois.add(poi);
        }
        return pois;
    }
}