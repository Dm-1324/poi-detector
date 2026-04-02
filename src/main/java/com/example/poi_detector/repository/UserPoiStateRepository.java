package com.example.poi_detector.repository;

import com.example.poi_detector.entity.UserPoiState;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserPoiStateRepository extends JpaRepository<UserPoiState, Long> {

//    Optional<UserPoiState> findByUserIdAndPoiId(String userId, String poiId);

//    boolean existsByUserIdAndPoiId(String userId, String poiId);

    UserPoiState findByUsernameAndPoiId(String username, String poiId);

    @Transactional
    void deleteByUsername(String username);
}
