package com.example.poi_detector.service;

import com.example.poi_detector.dto.POIResponse;

import java.util.List;

public interface POIService {

    List<POIResponse> getNearbyPOIs(double lat, double lon);
}