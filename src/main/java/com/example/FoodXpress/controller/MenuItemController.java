package com.example.FoodXpress.controller;

import com.example.FoodXpress.model.MenuItem;
import com.example.FoodXpress.model.Restaurant;
import com.example.FoodXpress.repository.MenuItemRepository;
import com.example.FoodXpress.repository.RestaurantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.io.File;
import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/menu")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class MenuItemController {

    @Autowired
    private MenuItemRepository menuItemRepo;

    @Autowired
    private RestaurantRepository restaurantRepo;

    @PostMapping("/add/{restaurantId}")
    public ResponseEntity<?> addMenuItem(
            @PathVariable Long restaurantId,
            @RequestParam("dishName") String dishName,
            @RequestParam("description") String description,
            @RequestParam("price") Double price,
            @RequestParam("available") boolean available,
            @RequestParam("image") MultipartFile imageFile
    ) {
        Optional<Restaurant> restaurantOpt = restaurantRepo.findById(restaurantId);
        if (restaurantOpt.isEmpty()) return ResponseEntity.notFound().build();

        try {
            String fileName = UUID.randomUUID() + "_" + imageFile.getOriginalFilename();
            String uploadPath = System.getProperty("user.dir") + "/uploads/menu_images/" + fileName;
//            File dest = new File(uploadPath);
//            dest.getParentFile().mkdirs();
            imageFile.transferTo(new File(uploadPath));

            MenuItem item = new MenuItem();
            item.setDishName(dishName);
            item.setDescription(description);
            item.setPrice(price);
            item.setAvailable(available);
            item.setImagePath(fileName);
            item.setRestaurant(restaurantOpt.get());

            return ResponseEntity.ok(menuItemRepo.save(item));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Failed to upload menu item");
        }
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
