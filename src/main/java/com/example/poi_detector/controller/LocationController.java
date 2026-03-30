package com.example.poi_detector.controller;

import com.example.poi_detector.dto.LocationRequest;
import com.example.poi_detector.service.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/location")
public class LocationController {

    @Autowired
    private LocationService locationService;

    @PostMapping("/update")
    public String updateLocation(@RequestBody LocationRequest request) {
        return locationService.processLocation(request);
    }
}