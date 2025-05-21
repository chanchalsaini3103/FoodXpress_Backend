package com.example.FoodXpress.controller;

import com.example.FoodXpress.model.Restaurant;
import com.example.FoodXpress.model.User;
import com.example.FoodXpress.repository.RestaurantRepository;
import com.example.FoodXpress.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "http://localhost:5173")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {


        @Autowired
        private UserRepository userRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

        // Returns all users with nested restaurant data (if any)
        @GetMapping("/users")
        public List<User> getAllUsersWithRestaurants() {
            return userRepository.findAll();
        }

    @PutMapping("/approve/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> approveRestaurant(@PathVariable Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setStatus(User.Status.ACTIVE);
        userRepository.save(user);

        restaurantRepository.findByUser_UserId(userId).ifPresentOrElse(restaurant -> {
            restaurant.setStatus(Restaurant.Status.ACTIVE);
            restaurantRepository.save(restaurant);
            System.out.println("✅ Restaurant status updated to ACTIVE for ID: " + restaurant.getId());
        }, () -> {
            System.out.println("❌ No restaurant found for user ID: " + userId);
        });

        return ResponseEntity.ok("Restaurant account approved!");
    }



}
