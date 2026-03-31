package com.example.poi_detector.service;

import com.example.poi_detector.dto.LocationRequest;

public interface LocationService {

    String processLocation(LocationRequest request);
}