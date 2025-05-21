package com.example.FoodXpress.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Serve images at http://localhost:8081/menu-images/filename.jpg
        registry.addResourceHandler("/menu-images/**")
                .addResourceLocations("file:" + System.getProperty("user.dir") + "/uploads/menu_images/");
    }
}
