package com.libraryManagement.project.service.impl;

import com.libraryManagement.project.dto.requestDTO.CartItemRequestDTO;
import com.libraryManagement.project.dto.responseDTO.CartItemResponseDTO;
import com.libraryManagement.project.dto.responseDTO.CartResponseDTO;
import com.libraryManagement.project.entity.Book;
import com.libraryManagement.project.entity.Cart;
import com.libraryManagement.project.entity.CartItems;
import com.libraryManagement.project.entity.User;
import com.libraryManagement.project.repository.BookRepository;
import com.libraryManagement.project.repository.CartRepository;
import com.libraryManagement.project.repository.UserRepository;
import com.libraryManagement.project.service.CartService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Service
@Transactional
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    public CartServiceImpl(CartRepository cartRepository, UserRepository userRepository, BookRepository bookRepository) {
        this.cartRepository = cartRepository;
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
    }

    @Override
    public CartResponseDTO getCart(Long userId) {
        Cart cart = getOrCreateCart(userId);
        return mapToCartResponseDTO(cart);
    }

    @Override
    public CartResponseDTO addToCart(Long userId, CartItemRequestDTO requestDTO) {
        Cart cart = getOrCreateCart(userId);
        Book book = bookRepository.findById(requestDTO.getBookId())
                .orElseThrow(() -> new EntityNotFoundException("Book not found"));

        CartItems existingItem = cart.getCartItems().stream()
                .filter(item -> item.getBook().getBookId().equals(book.getBookId()))
                .findFirst()
                .orElse(null);

        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + requestDTO.getQuantity());
        } else {
            CartItems newItem = new CartItems();
            newItem.setBook(book);
            newItem.setQuantity(requestDTO.getQuantity());
            cart.addCartItem(newItem);
        }

        cart.setUpdatedAt(LocalDateTime.now());
        cartRepository.save(cart);
        return mapToCartResponseDTO(cart);
    }

    @Override
    public CartResponseDTO updateCartItem(Long userId, Long bookId, Integer quantity) {
        Cart cart = getOrCreateCart(userId);

        CartItems item = cart.getCartItems().stream()
                .filter(ci -> ci.getBook().getBookId().equals(bookId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Cart item not found"));

        if (quantity <= 0) {
            cart.removeCartItem(item);
        } else {
            item.setQuantity(quantity);
        }

        cart.setUpdatedAt(LocalDateTime.now());
        cartRepository.save(cart);
        return mapToCartResponseDTO(cart);
    }

    @Override
    public CartResponseDTO removeCartItem(Long userId, Long bookId) {
        Cart cart = getOrCreateCart(userId);

        CartItems item = cart.getCartItems().stream()
                .filter(ci -> ci.getBook().getBookId().equals(bookId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Cart item not found"));

        cart.removeCartItem(item);
        cart.setUpdatedAt(LocalDateTime.now());
        cartRepository.save(cart);
        return mapToCartResponseDTO(cart);
    }

    @Override
    public CartResponseDTO clearCart(Long userId) {
        Cart cart = getOrCreateCart(userId);
        cart.getCartItems().clear();
        cart.setUpdatedAt(LocalDateTime.now());
        cartRepository.save(cart);
        return mapToCartResponseDTO(cart);
    }

    private Cart getOrCreateCart(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        return cartRepository.findByUser(user)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUser(user);
                    newCart.setCreatedAt(LocalDateTime.now());
                    newCart.setUpdatedAt(LocalDateTime.now());
                    return cartRepository.save(newCart);
                });
    }

    private CartResponseDTO mapToCartResponseDTO(Cart cart) {
        CartResponseDTO dto = new CartResponseDTO();
        dto.setCartId(cart.getCartId());
        dto.setUserId(cart.getUser().getUserId());
        dto.setUpdatedAt(cart.getUpdatedAt());
        dto.setItemCount(cart.getCartItems().size());
        dto.setTotalAmount(cart.getCartItems().stream()
                .mapToDouble(item -> item.getBook().getPrice() * item.getQuantity())
                .sum());

        dto.setCartItems(cart.getCartItems().stream().map(item -> {
            CartItemResponseDTO itemDTO = new CartItemResponseDTO();
            itemDTO.setCartItemId(item.getCartItemId());
            itemDTO.setBookId(item.getBook().getBookId());
            itemDTO.setBookTitle(item.getBook().getTitle());
            itemDTO.setPrice(item.getBook().getPrice());
            itemDTO.setQuantity(item.getQuantity());
            itemDTO.setSubtotal(item.getBook().getPrice() * item.getQuantity());
            return itemDTO;
        }).collect(Collectors.toList()));

        return dto;
    }
}
