package com.example.demo.Controller;


import com.example.demo.DTO.ApiResponse;
import com.example.demo.DTO.CartDTO;
import com.example.demo.DTO.CartItemDTO;
import com.example.demo.Security.CurrentUser;
import com.example.demo.Security.UserPrincipal;
import com.example.demo.service.CartService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    @Autowired
    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    public ResponseEntity<CartDTO> getCart(@CurrentUser UserPrincipal currentUser) {
        CartDTO cart = cartService.getCartByUserId(currentUser.getId());
        return ResponseEntity.ok(cart);
    }

    @PostMapping("/items")
    public ResponseEntity<CartDTO> addItemToCart(
            @CurrentUser UserPrincipal currentUser,
            @Valid @RequestBody CartItemDTO cartItemDTO) {
        try {
            CartDTO cart = cartService.addItemToCart(currentUser.getId(), cartItemDTO);
            return ResponseEntity.ok(cart);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/items/{itemId}")
    public ResponseEntity<CartDTO> updateCartItem(
            @CurrentUser UserPrincipal currentUser,
            @PathVariable Long itemId,
            @Valid @RequestBody CartItemDTO cartItemDTO) {
        try {
            CartDTO cart = cartService.updateCartItem(currentUser.getId(), itemId, cartItemDTO);
            return ResponseEntity.ok(cart);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<CartDTO> removeItemFromCart(
            @CurrentUser UserPrincipal currentUser,
            @PathVariable Long itemId) {
        try {
            CartDTO cart = cartService.removeItemFromCart(currentUser.getId(), itemId);
            return ResponseEntity.ok(cart);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/clear")
    public ResponseEntity<ApiResponse> clearCart(@CurrentUser UserPrincipal currentUser) {
        cartService.clearCart(currentUser.getId());
        return ResponseEntity.ok(new ApiResponse(true, "Cart cleared successfully"));
    }

    @GetMapping("/count")
    public ResponseEntity<Long> getCartItemCount(@CurrentUser UserPrincipal currentUser) {
        Long count = cartService.getCartItemCount(currentUser.getId());
        return ResponseEntity.ok(count);
    }
}
