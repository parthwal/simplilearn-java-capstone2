package com.example.assignment.dao;

import com.example.assignment.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductDAO extends JpaRepository<Product, Long> { }
