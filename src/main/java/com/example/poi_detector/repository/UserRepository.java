package com.example.poi_detector.repository;

import com.example.poi_detector.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {
}