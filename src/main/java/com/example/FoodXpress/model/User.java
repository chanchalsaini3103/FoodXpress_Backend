package com.example.FoodXpress.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Entity
@Table(name = "users")
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(nullable = false)
    private String fullName;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    private Role role = Role.CUSTOMER;

    @Enumerated(EnumType.STRING)
    private Status status = Status.ACTIVE;

    public enum Role {
        ADMIN, CUSTOMER, RESTAURANT
    }

    public enum Status {
        ACTIVE, INACTIVE
    }

    private Timestamp createdAt = new Timestamp(System.currentTimeMillis());

    @OneToOne(mappedBy = "user")
    @JsonManagedReference
    private Restaurant restaurant;


}
