package com.example.FoodXpress.controller;

import com.example.FoodXpress.model.MenuItem;
import com.example.FoodXpress.model.Restaurant;
import com.example.FoodXpress.repository.MenuItemRepository;
import com.example.FoodXpress.repository.RestaurantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/menu")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class MenuItemController {

    @Autowired
    private MenuItemRepository menuItemRepo;

    @Autowired
    private RestaurantRepository restaurantRepo;

    @PostMapping("/add/{restaurantId}")
    public ResponseEntity<?> addMenuItem(@PathVariable Long restaurantId, @RequestBody MenuItem menuItem) {
        Optional<Restaurant> restaurant = restaurantRepo.findById(restaurantId);
        if (restaurant.isEmpty()) return ResponseEntity.notFound().build();

        menuItem.setRestaurant(restaurant.get());
        return ResponseEntity.ok(menuItemRepo.save(menuItem));
    }

    @GetMapping("/restaurant/{restaurantId}")
    public List<MenuItem> getRestaurantMenu(@PathVariable Long restaurantId) {
        return menuItemRepo.findByRestaurant(restaurantRepo.findById(restaurantId).orElseThrow());
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateMenuItem(@PathVariable Long id, @RequestBody MenuItem updatedItem) {
        Optional<MenuItem> existing = menuItemRepo.findById(id);
        if (existing.isEmpty()) return ResponseEntity.notFound().build();

        MenuItem item = existing.get();
        item.setDishName(updatedItem.getDishName());
        item.setDescription(updatedItem.getDescription());
        item.setPrice(updatedItem.getPrice());
        item.setAvailable(updatedItem.isAvailable());

        return ResponseEntity.ok(menuItemRepo.save(item));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteMenuItem(@PathVariable Long id) {
        if (!menuItemRepo.existsById(id)) return ResponseEntity.notFound().build();
        menuItemRepo.deleteById(id);
        return ResponseEntity.ok("Item deleted");
    }
}
