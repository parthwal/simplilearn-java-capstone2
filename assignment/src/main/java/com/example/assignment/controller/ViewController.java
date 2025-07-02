package com.example.assignment.controller;

import com.example.assignment.dao.*;
import com.example.assignment.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class ViewController {

    @Autowired private ProductDAO productDAO;
    @Autowired private UserDAO userDAO;
    @Autowired private PurchaseDAO purchaseDAO;

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/products")
    public String viewProducts(Model model) {
        model.addAttribute("products", productDAO.findAll());
        return "products";
    }

    @GetMapping("/products/add")
    public String addProductForm() {
        return "add-product";
    }

    @PostMapping("/products/save")
    public String saveProduct(@RequestParam String name,
                              @RequestParam String category,
                              @RequestParam double price) {
        Product product = new Product();
        product.setName(name);
        product.setCategory(category);
        product.setPrice(price);
        productDAO.save(product);
        return "redirect:/products";
    }

    @GetMapping("/users")
    public String searchUsers(@RequestParam(required = false) String keyword, Model model) {
        if (keyword != null && !keyword.isEmpty()) {
            model.addAttribute("users", userDAO.findByNameContaining(keyword));
        }
        return "users"; // template to be created
    }

    @GetMapping("/purchases")
    public String viewPurchasesForm() {
        return "purchases"; // template to be created
    }
}
