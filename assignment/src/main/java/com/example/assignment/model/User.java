package com.example.assignment.model;

import jakarta.persistence.*;

@Entity
public class User {
    @Id @GeneratedValue
    private Long id;

    private String name;
    private String email;

    // Getters and Setters
}
