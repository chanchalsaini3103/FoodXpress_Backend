package com.example.FoodXpress.repository;

import com.example.FoodXpress.model.Restaurant;
import com.example.FoodXpress.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
    Optional<Restaurant> findByEmail(String email);



    Optional<Restaurant> findByUser(User user);
    Optional<Restaurant> findByUser_UserId(Long userId);

}
