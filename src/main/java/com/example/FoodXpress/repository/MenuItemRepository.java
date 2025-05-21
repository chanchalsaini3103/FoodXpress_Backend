package com.example.FoodXpress.repository;

import com.example.FoodXpress.model.MenuItem;
import com.example.FoodXpress.model.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {
    List<MenuItem> findByRestaurant(Restaurant restaurant);
}
