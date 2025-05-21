package com.example.FoodXpress.controller;

import com.example.FoodXpress.model.Restaurant;
import com.example.FoodXpress.model.User;
import com.example.FoodXpress.repository.RestaurantRepository;
import com.example.FoodXpress.repository.UserRepository;
import com.example.FoodXpress.security.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public String register(@RequestBody User user) {
        user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
        userRepo.save(user);
        return "User registered successfully!";
    }

    @PostMapping("/restaurant-register")
    public ResponseEntity<String> registerRestaurant(@RequestBody Restaurant restaurant) {
        if (restaurant.getEmail() == null || restaurant.getPasswordHash() == null) {
            return ResponseEntity.badRequest().body("Email and password are required.");
        }

        // Check if email already exists
        if (userRepo.findByEmail(restaurant.getEmail()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already registered.");
        }

        // 1. Create user entity
        User user = new User();
        user.setEmail(restaurant.getEmail());
        user.setPasswordHash(passwordEncoder.encode(restaurant.getPasswordHash())); // from @JsonProperty
        user.setFullName(restaurant.getRestaurantName());
        user.setRole(User.Role.RESTAURANT);
        user.setStatus(User.Status.INACTIVE); // needs admin approval

        userRepo.save(user); // save user first to get ID

        // 2. Create restaurant and link user
        restaurant.setUser(user);
        restaurant.setStatus(Restaurant.Status.INACTIVE);
        restaurantRepository.save(restaurant);

        return ResponseEntity.ok("Restaurant registered successfully. Awaiting admin approval.");
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User loginUser) {
        User user = userRepo.findByEmail(loginUser.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        if (!user.getStatus().equals(User.Status.ACTIVE)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Account not yet approved by admin.");
        }

        if (passwordEncoder.matches(loginUser.getPasswordHash(), user.getPasswordHash())) {
            String token = jwtService.generateToken(
                    user.getEmail(),
                    user.getRole().name(), // ✅ convert Role enum to String
                    user.getUserId()
            );

            return ResponseEntity.ok(token);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }


    @PutMapping("/admin/approve/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String approveRestaurant(@PathVariable Long id) {
        User user = userRepo.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        user.setStatus(User.Status.ACTIVE);
        userRepo.save(user);
        return "Restaurant account approved!";
    }

}

