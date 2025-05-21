package com.example.FoodXpress.controller;

import com.example.FoodXpress.model.Restaurant;
import com.example.FoodXpress.model.User;
import com.example.FoodXpress.repository.RestaurantRepository;
import com.example.FoodXpress.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/restaurant")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class RestaurantController {

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private RestaurantRepository restaurantRepo;

    @Autowired
    private UserRepository userRepository;

    // ✅ Get all pending restaurants (for admin panel)
    @GetMapping("/pending")
    public List<Restaurant> getPendingRestaurants() {
        return restaurantRepo.findAll().stream()
                .filter(r -> r.getStatus() == Restaurant.Status.INACTIVE)
                .toList();
    }

    // ✅ Get all active restaurants
    @GetMapping("/active")
    public List<Restaurant> getActiveRestaurants() {
        return restaurantRepo.findAll().stream()
                .filter(r -> r.getStatus() == Restaurant.Status.ACTIVE)
                .toList();
    }

    @PostMapping("/complete-profile/{userId}")
    public ResponseEntity<?> completeRestaurantProfile(
            @PathVariable Long userId,
            @RequestParam("licenseFile") MultipartFile licenseFile,
            @RequestParam("description") String description,
            @RequestParam("address") String address) {

        try {
            Optional<Restaurant> optional = restaurantRepo.findByUser_UserId(userId);
            if (optional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Restaurant not found");
            }

            Restaurant restaurant = optional.get();

            if (licenseFile == null || licenseFile.isEmpty()) {
                return ResponseEntity.badRequest().body("License file is required.");
            }

            // ✅ Absolute path
            String fileName = UUID.randomUUID() + "_" + licenseFile.getOriginalFilename();
            String uploadDir = System.getProperty("user.dir") + "/uploads/licenses/";
            String uploadPath = uploadDir + fileName;

            File dest = new File(uploadPath);
            dest.getParentFile().mkdirs();
            licenseFile.transferTo(dest);

            restaurant.setLicenseFilePath(uploadPath);
            restaurant.setDescription(description);
            restaurant.setAddress(address);
            restaurant.setProfileComplete(true);

            restaurantRepo.save(restaurant);
            return ResponseEntity.ok("Profile completed successfully!");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to complete profile: " + e.getMessage());
        }
    }


    // ✅ Optional: Get current logged-in restaurant using JWT Principal
    @GetMapping("/me")
    public ResponseEntity<?> getRestaurantByToken(Principal principal) {
        Optional<User> user = userRepository.findByEmail(principal.getName());
        if (user.isEmpty()) return ResponseEntity.notFound().build();

        Optional<Restaurant> restaurant = restaurantRepo.findByUser(user.get());
        if (restaurant.isPresent()) {
            restaurant.get().setUser(user.get()); // ✅ ensure user.userId is included in frontend
            return ResponseEntity.ok(restaurant.get());
        }

        return ResponseEntity.notFound().build();
    }

}

