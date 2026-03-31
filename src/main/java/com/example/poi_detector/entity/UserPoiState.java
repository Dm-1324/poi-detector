package com.example.poi_detector.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_poi_state")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserPoiState {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String poiId;
    private String poiName;
    private boolean entered;

    private LocalDateTime lastEnteredAt;
}