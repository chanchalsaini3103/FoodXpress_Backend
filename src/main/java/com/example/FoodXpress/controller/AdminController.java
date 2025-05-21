package com.example.FoodXpress.controller;

import com.example.FoodXpress.model.Restaurant;
import com.example.FoodXpress.model.User;
import com.example.FoodXpress.repository.RestaurantRepository;
import com.example.FoodXpress.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
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

        // Returns all users with nested restaurant data (if any)
        @GetMapping("/users")
        public List<User> getAllUsersWithRestaurants() {
            return userRepository.findAll();
        }
    }
