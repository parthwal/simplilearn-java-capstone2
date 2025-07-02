package com.example.assignment.dao;

import com.example.assignment.model.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface PurchaseDAO extends JpaRepository<Purchase, Long> {
    List<Purchase> findByDateBetween(LocalDate start, LocalDate end);
    List<Purchase> findByProductCategory(String category);
}
