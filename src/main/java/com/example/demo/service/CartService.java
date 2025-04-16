package com.example.demo.service;


import com.example.demo.DTO.CartDTO;
import com.example.demo.DTO.CartItemDTO;
import com.example.demo.entity.Cart;

public interface CartService {

    CartDTO getCartByUserId(Long userId);

    CartDTO addItemToCart(Long userId, CartItemDTO cartItemDTO);

    CartDTO updateCartItem(Long userId, Long cartItemId, CartItemDTO cartItemDTO);

    CartDTO removeItemFromCart(Long userId, Long cartItemId);

    void clearCart(Long userId);

    Long getCartItemCount(Long userId);
}