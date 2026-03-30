package com.example.poi_detector.dto;

import lombok.Data;

@Data
public class LocationRequest {
    private String userId;
    private double latitude;
    private double longitude;
}