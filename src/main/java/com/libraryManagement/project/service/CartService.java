package com.libraryManagement.project.service;

import com.libraryManagement.project.dto.requestDTO.CartItemRequestDTO;
import com.libraryManagement.project.dto.responseDTO.CartResponseDTO;

public interface CartService {

    CartResponseDTO getCart(Long userId);

    CartResponseDTO addToCart(Long userId, CartItemRequestDTO requestDTO);

    CartResponseDTO updateCartItem(Long userId, Long bookId, Integer quantity);

    CartResponseDTO removeCartItem(Long userId, Long bookId);

    CartResponseDTO clearCart(Long userId);
}
