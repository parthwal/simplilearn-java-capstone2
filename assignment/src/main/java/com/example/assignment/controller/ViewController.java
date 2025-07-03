package com.example.assignment.controller;

import com.example.assignment.dao.ProductDAO;
import com.example.assignment.dao.UserDAO;
import com.example.assignment.dao.PurchaseDAO;
import com.example.assignment.model.Product;
import com.example.assignment.model.User;
import com.example.assignment.model.Purchase;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
public class ViewController {

    @Autowired
    private ProductDAO productDAO;

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private PurchaseDAO purchaseDAO;
    @GetMapping("/")
    public String home(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user != null) {
            return user.getRole().equals("ADMIN") ? "redirect:/dashboard" : "redirect:/user-home";
        }
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String handleLogin(@RequestParam String email,
                              @RequestParam String password,
                              HttpSession session) {
    	
    	if (email.equals("admin@admin.com") && password.equals("admin")) {
    	    User admin = new User();
    	    admin.setName("Admin");
    	    admin.setEmail("admin@admin.com");
    	    admin.setPassword("admin");
    	    admin.setRole("ADMIN");
    	    session.setAttribute("user", admin);
    	    return "redirect:/dashboard";
    	}

        User user = userDAO.findByEmailAndPassword(email, password);
        if (user == null) {
            return "redirect:/login?error=true";
        }

        session.setAttribute("user", user);
        return "redirect:/user-home";
    }

    @PostMapping("/signup")
    public String handleSignup(@RequestParam String name,
                               @RequestParam String email,
                               @RequestParam String password) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);
        user.setRole("USER");
        userDAO.save(user);
        return "redirect:/login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
    @PostMapping("/change-password")
    public String changePassword(@RequestParam String email,
                                 @RequestParam String oldPassword,
                                 @RequestParam String newPassword,
                                 HttpSession session) {

        if (email.equals("admin@admin.com") && oldPassword.equals("admin")) {
            session.setAttribute("adminPassword", newPassword);
            return "redirect:/login?message=Admin password changed";
        }

        User user = userDAO.findByEmailAndPassword(email, oldPassword);
        if (user != null) {
            user.setPassword(newPassword);
            userDAO.save(user);
            return "redirect:/login?message=User password changed";
        }

        return "redirect:/login?error=Invalid credentials";
    }


    @GetMapping("/dashboard")
    public String dashboard(HttpSession session) {
    	User user = (User) session.getAttribute("user");
    	if (user == null || !"ADMIN".equals(user.getRole())) return "redirect:/login";
        return "dashboard";
    }

    @GetMapping("/user-home")
    public String userHome(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || !"USER".equals(user.getRole())) return "redirect:/login";

        model.addAttribute("products", productDAO.findAll());
        return "user-home";
    }
    @PostMapping("/purchase")
    public String makePurchase(@RequestParam Long productId,
                               @RequestParam int qty,
                               HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || !"USER".equals(user.getRole())) return "redirect:/login";

        Product product = productDAO.findById(productId).orElse(null);
        if (product == null || product.getQuantity() < qty) return "redirect:/user-home";

        product.setQuantity(product.getQuantity() - qty);
        productDAO.save(product);

        Purchase purchase = new Purchase();
        purchase.setProduct(product);
        purchase.setUser(user);
        purchase.setDate(LocalDate.now());
        purchase.setQuantity(qty);
        purchaseDAO.save(purchase);

        return "redirect:/user-home";
    }


    @GetMapping("/products")
    public String showProducts(Model model, HttpSession session) {
    	User user = (User) session.getAttribute("user");
    	if (user == null || !"ADMIN".equals(user.getRole())) return "redirect:/login";
        model.addAttribute("products", productDAO.findAll());
        return "products";
    }

    @PostMapping("/products/add")
    public String saveProduct(@RequestParam String name,
                              @RequestParam String category,
                              @RequestParam double price,
                              @RequestParam int quantity,
                              HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || !user.getRole().equals("ADMIN")) return "redirect:/login";

        Product product = new Product();
        product.setName(name);
        product.setCategory(category);
        product.setPrice(price);
        product.setQuantity(quantity);
        productDAO.save(product);

        return "redirect:/products";
    }

    @GetMapping("/users")
    public String showUsers(@RequestParam(required = false) String keyword,
                            Model model, HttpSession session) {
    	User user = (User) session.getAttribute("user");
    	if (user == null || !"ADMIN".equals(user.getRole())) return "redirect:/login";

        List<User> users;
        if (keyword != null && !keyword.isEmpty()) {
        	users = userDAO.findByNameContainingIgnoreCase(keyword);
        } else {
            users = userDAO.findAll();
        }

        model.addAttribute("users", users);
        return "users";
    }

    @GetMapping("/purchases")
    public String showPurchases(@RequestParam(required = false) String category,
                                @RequestParam(required = false) String start,
                                @RequestParam(required = false) String end,
                                Model model,
                                HttpSession session) {
    	User user = (User) session.getAttribute("user");
    	if (user == null || !"ADMIN".equals(user.getRole())) return "redirect:/login";

        List<Purchase> purchases;

        if (category != null && start != null && end != null) {
            purchases = purchaseDAO.findByProductCategoryAndDateBetween(
                    category, LocalDate.parse(start), LocalDate.parse(end));
        } else if (start != null && end != null) {
            purchases = purchaseDAO.findByDateBetween(LocalDate.parse(start), LocalDate.parse(end));
        } else if (category != null) {
            purchases = purchaseDAO.findByProductCategory(category);
        } else {
            purchases = purchaseDAO.findAll();
        }

        model.addAttribute("purchases", purchases);
        return "purchases";
    }
}
