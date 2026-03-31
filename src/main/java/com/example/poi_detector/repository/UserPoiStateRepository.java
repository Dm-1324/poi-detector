package com.example.poi_detector.repository;

import com.example.poi_detector.entity.UserPoiState;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserPoiStateRepository extends JpaRepository<UserPoiState, Long> {

//    Optional<UserPoiState> findByUserIdAndPoiId(String userId, String poiId);

//    boolean existsByUserIdAndPoiId(String userId, String poiId);

    UserPoiState findByUserIdAndPoiId(String userId, String poiId);
}
