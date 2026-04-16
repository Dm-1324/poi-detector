package com.example.poi_detector.controller;

import com.example.poi_detector.dto.AuthRequest;
import com.example.poi_detector.entity.User;
import com.example.poi_detector.repository.UserPoiStateRepository;
import com.example.poi_detector.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserPoiStateRepository userPoiStateRepository;

    @PostMapping("/register")
    public User register(@RequestBody AuthRequest request) {
        if (userRepository.existsById(request.getUsername())) {
            throw new RuntimeException("User already exists");
        }
        User user = new User();
        user.setUsername(request.getUsername());

        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setConsent(false);

        return userRepository.save(user);
    }

    @PostMapping("/login")
    @Transactional
    public User login(@RequestBody AuthRequest request) {
        User user = userRepository.findById(request.getUsername()).orElse(null);

        if (user != null && passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            userPoiStateRepository.deleteByUsername(user.getUsername());
            return user;
        }
        throw new RuntimeException("Invalid credentials");
    }

    @PostMapping("/consent")
    public User giveConsent(@RequestParam String username) {
        User user = userRepository.findById(username).orElseThrow();
        user.setConsent(true);
        return userRepository.save(user);
    }
}