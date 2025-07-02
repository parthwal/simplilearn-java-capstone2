package com.example.assignment.model;

import jakarta.persistence.*;
@Entity
public class Purchase {
    @Id @GeneratedValue
    private Long id;

    @ManyToOne
    private User user;

    @ManyToOne
    private Product product;

}
