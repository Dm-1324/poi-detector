package com.example.poi_detector.dto;

import lombok.Data;

@Data
public class POIResponse {

    private String id;
    private String name;
    private double latitude;
    private double longitude;

    public String getUniqueKey() {
        return name + "_" + latitude + "_" + longitude;
    }
}