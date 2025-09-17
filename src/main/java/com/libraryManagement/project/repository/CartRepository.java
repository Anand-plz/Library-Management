package com.libraryManagement.project.repository;

import com.libraryManagement.project.entity.Cart;
import com.libraryManagement.project.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUser(User user);
}
