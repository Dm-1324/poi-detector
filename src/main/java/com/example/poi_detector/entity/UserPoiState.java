package com.example.poi_detector.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_poi_state")
@Data
public class UserPoiState {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userId;
    private String poiId;
    private boolean entered;

    private LocalDateTime lastUpdated;
}