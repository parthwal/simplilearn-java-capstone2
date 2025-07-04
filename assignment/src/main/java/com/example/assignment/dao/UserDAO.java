package com.example.assignment.dao;

import com.example.assignment.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserDAO extends JpaRepository<User, Long> {
    List<User> findByNameContainingIgnoreCase(String keyword);
    User findByEmailAndPassword(String email, String password);
}
