package com.libraryManagement.project.controller;

import com.libraryManagement.project.dto.requestDTO.CartItemRequestDTO;
import com.libraryManagement.project.dto.responseDTO.CartResponseDTO;
import com.libraryManagement.project.service.CartService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<CartResponseDTO> getCart(@PathVariable Long userId) {
        return ResponseEntity.ok(cartService.getCart(userId));
    }

    @PostMapping("/{userId}/items")
    public ResponseEntity<CartResponseDTO> addToCart(
            @PathVariable Long userId,
            @RequestBody CartItemRequestDTO requestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(cartService.addToCart(userId, requestDTO));
    }

    @PutMapping("/{userId}/items/{bookId}")
    public ResponseEntity<CartResponseDTO> updateCartItem(
            @PathVariable Long userId,
            @PathVariable Long bookId,
            @RequestBody CartItemRequestDTO requestDTO) {
        return ResponseEntity.ok(cartService.updateCartItem(userId, bookId, requestDTO.getQuantity()));
    }

    @DeleteMapping("/{userId}/items/{bookId}")
    public ResponseEntity<CartResponseDTO> removeCartItem(
            @PathVariable Long userId,
            @PathVariable Long bookId) {
        return ResponseEntity.ok(cartService.removeCartItem(userId, bookId));
    }

    @DeleteMapping("/{userId}/clear")
    public ResponseEntity<CartResponseDTO> clearCart(@PathVariable Long userId) {
        return ResponseEntity.ok(cartService.clearCart(userId));
    }
}
