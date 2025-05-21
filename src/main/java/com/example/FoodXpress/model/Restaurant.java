package com.example.FoodXpress.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "restaurants")
@Data
public class Restaurant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String restaurantName;
    private String city;
    private String licenseId;
    private String contactNumber;

    @Transient
    @JsonProperty
    private String passwordHash;


    @Column(nullable = false, unique = true)
    private String email; // used to link with login

    @Enumerated(EnumType.STRING)
    private Status status = Status.INACTIVE;

    public enum Status {
        ACTIVE, INACTIVE
    }


    private String licenseFilePath;
    private String description;
    private String address;
    private boolean profileComplete = false;


    @OneToOne
    @JoinColumn(name = "userId", referencedColumnName = "userId")
    @JsonBackReference
    private User user;

}
