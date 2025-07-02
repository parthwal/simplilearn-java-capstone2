package com.example.assignment.controller;

import com.example.assignment.model.*;
import com.example.assignment.dao.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired private ProductDAO productDAO;
    @Autowired private UserDAO userDAO;
    @Autowired private PurchaseDAO purchaseDAO;

    @PostMapping("/products")
    public Product addProduct(@RequestBody Product product) {
        return productDAO.save(product);
    }

    @GetMapping("/products")
    public List<Product> getAllProducts() {
        return productDAO.findAll();
    }

    @GetMapping("/users")
    public List<User> searchUsers(@RequestParam String keyword) {
        return userDAO.findByNameContaining(keyword);
    }

    @GetMapping("/purchases/date")
    public List<Purchase> getByDate(
        @RequestParam String start,
        @RequestParam String end
    ) {
        return purchaseDAO.findByDateBetween(LocalDate.parse(start), LocalDate.parse(end));
    }

    @GetMapping("/purchases/category")
    public List<Purchase> getByCategory(@RequestParam String category) {
        return purchaseDAO.findByProductCategory(category);
    }
}
