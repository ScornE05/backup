package com.example.demo.service.impl;

import com.example.demo.DTO.CartDTO;
import com.example.demo.DTO.CartItemDTO;
import com.example.demo.entity.Cart;
import com.example.demo.entity.CartItem;
import com.example.demo.entity.Pet;
import com.example.demo.entity.Product;
import com.example.demo.entity.User;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.CartItemRepository;
import com.example.demo.repository.CartRepository;
import com.example.demo.repository.PetRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.CartService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final PetRepository petRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public CartServiceImpl(CartRepository cartRepository,
                           CartItemRepository cartItemRepository,
                           UserRepository userRepository,
                           ProductRepository productRepository,
                           PetRepository petRepository,
                           ModelMapper modelMapper) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.petRepository = petRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public CartDTO getCartByUserId(Long userId) {
        // Get cart or create if not exists
        Cart cart = getOrCreateCart(userId);

        return convertToCartDTO(cart);
    }

    @Override
    @Transactional
    public CartDTO addItemToCart(Long userId, CartItemDTO cartItemDTO) {
        Cart cart = getOrCreateCart(userId);

        // Validate that either productId or petId is provided
        if (cartItemDTO.getProductId() == null && cartItemDTO.getPetId() == null) {
            throw new IllegalArgumentException("Either product or pet must be specified");
        }

        CartItem cartItem;

        // Check if product already in cart
        if (cartItemDTO.getProductId() != null) {
            Optional<CartItem> existingItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), cartItemDTO.getProductId());

            if (existingItem.isPresent()) {
                // Update quantity if already in cart
                cartItem = existingItem.get();
                cartItem.setQuantity(cartItem.getQuantity() + cartItemDTO.getQuantity());
            } else {
                // Add new item
                Product product = productRepository.findById(cartItemDTO.getProductId())
                        .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + cartItemDTO.getProductId()));

                // Check stock
                if (product.getQuantityInStock() < cartItemDTO.getQuantity()) {
                    throw new IllegalStateException("Insufficient stock for product: " + product.getName());
                }

                cartItem = new CartItem();
                cartItem.setCart(cart);
                cartItem.setProduct(product);
                cartItem.setQuantity(cartItemDTO.getQuantity());
            }
        }
        // Check if pet already in cart
        else {
            Optional<CartItem> existingItem = cartItemRepository.findByCartIdAndPetId(cart.getId(), cartItemDTO.getPetId());

            if (existingItem.isPresent()) {
                // Update quantity if already in cart
                cartItem = existingItem.get();
                cartItem.setQuantity(cartItem.getQuantity() + cartItemDTO.getQuantity());
            } else {
                // Add new item
                Pet pet = petRepository.findById(cartItemDTO.getPetId())
                        .orElseThrow(() -> new ResourceNotFoundException("Pet not found with id: " + cartItemDTO.getPetId()));

                // Check if pet is available
                if (!"AVAILABLE".equals(pet.getStatus())) {
                    throw new IllegalStateException("Pet is not available: " + pet.getName());
                }

                cartItem = new CartItem();
                cartItem.setCart(cart);
                cartItem.setPet(pet);
                cartItem.setQuantity(cartItemDTO.getQuantity());
            }
        }

        // Save cart item
        cartItemRepository.save(cartItem);

        // Update cart timestamp
        cart.setUpdatedAt(LocalDateTime.now());
        cartRepository.save(cart);

        return convertToCartDTO(cart);
    }

    @Override
    @Transactional
    public CartDTO updateCartItem(Long userId, Long cartItemId, CartItemDTO cartItemDTO) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found for user id: " + userId));

        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found with id: " + cartItemId));

        // Verify cart item belongs to user's cart
        if (!cartItem.getCart().getId().equals(cart.getId())) {
            throw new IllegalArgumentException("Cart item does not belong to user's cart");
        }

        // Update quantity
        if (cartItemDTO.getQuantity() != null && cartItemDTO.getQuantity() > 0) {
            // Check stock if product
            if (cartItem.getProduct() != null) {
                Product product = cartItem.getProduct();
                if (product.getQuantityInStock() < cartItemDTO.getQuantity()) {
                    throw new IllegalStateException("Insufficient stock for product: " + product.getName());
                }
            }

            cartItem.setQuantity(cartItemDTO.getQuantity());
            cartItemRepository.save(cartItem);

            // Update cart timestamp
            cart.setUpdatedAt(LocalDateTime.now());
            cartRepository.save(cart);
        }

        return convertToCartDTO(cart);
    }

    @Override
    @Transactional
    public CartDTO removeItemFromCart(Long userId, Long cartItemId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found for user id: " + userId));

        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found with id: " + cartItemId));

        // Verify cart item belongs to user's cart
        if (!cartItem.getCart().getId().equals(cart.getId())) {
            throw new IllegalArgumentException("Cart item does not belong to user's cart");
        }

        // Remove cart item
        cartItemRepository.delete(cartItem);

        // Update cart timestamp
        cart.setUpdatedAt(LocalDateTime.now());
        cartRepository.save(cart);

        return convertToCartDTO(cart);
    }

    @Override
    @Transactional
    public void clearCart(Long userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found for user id: " + userId));

        // Delete all cart items
        cartItemRepository.deleteAllByCartId(cart.getId());

        // Update cart timestamp
        cart.setUpdatedAt(LocalDateTime.now());
        cartRepository.save(cart);
    }

    @Override
    public Long getCartItemCount(Long userId) {
        return cartRepository.countTotalItemsByUserId(userId);
    }

    // Helper methods
    private Cart getOrCreateCart(Long userId) {
        // Find existing cart
        Optional<Cart> existingCart = cartRepository.findByUserIdWithCartItems(userId);

        if (existingCart.isPresent()) {
            return existingCart.get();
        }

        // Create new cart if not exists
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        Cart newCart = new Cart();
        newCart.setUser(user);
        newCart.setCreatedAt(LocalDateTime.now());
        newCart.setUpdatedAt(LocalDateTime.now());

        return cartRepository.save(newCart);
    }

    private CartDTO convertToCartDTO(Cart cart) {
        CartDTO cartDTO = new CartDTO();
        cartDTO.setId(cart.getId());
        cartDTO.setUserId(cart.getUser().getId());
        cartDTO.setCreatedAt(cart.getCreatedAt());
        cartDTO.setUpdatedAt(cart.getUpdatedAt());

        List<CartItemDTO> cartItemDTOs = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;
        int totalItems = 0;

        for (CartItem item : cart.getCartItems()) {
            CartItemDTO itemDTO = new CartItemDTO();
            itemDTO.setId(item.getId());
            itemDTO.setCartId(cart.getId());
            itemDTO.setQuantity(item.getQuantity());

            // Set product details if available
            if (item.getProduct() != null) {
                Product product = item.getProduct();
                itemDTO.setProductId(product.getId());
                itemDTO.setProductName(product.getName());
                itemDTO.setProductImageUrl(product.getImageUrl());
                itemDTO.setProductPrice(product.getPrice());

                if (product.getDiscountPrice() != null) {
                    itemDTO.setProductDiscountPrice(product.getDiscountPrice());
                    itemDTO.setTotalPrice(product.getDiscountPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
                    totalAmount = totalAmount.add(product.getDiscountPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
                } else {
                    itemDTO.setTotalPrice(product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
                    totalAmount = totalAmount.add(product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
                }
            }

            // Set pet details if available
            if (item.getPet() != null) {
                Pet pet = item.getPet();
                itemDTO.setPetId(pet.getId());
                itemDTO.setPetName(pet.getName());
                itemDTO.setPetImageUrl(pet.getImageUrl());
                itemDTO.setPetPrice(pet.getPrice());
                itemDTO.setTotalPrice(pet.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));

                totalAmount = totalAmount.add(pet.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
            }

            cartItemDTOs.add(itemDTO);
            totalItems += item.getQuantity();
        }

        cartDTO.setItems(cartItemDTOs);
        cartDTO.setTotalAmount(totalAmount);
        cartDTO.setTotalItems(totalItems);

        return cartDTO;
    }
}