package com.example.demo.repository;

import com.example.demo.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    Optional<Cart> findByUserId(Long userId);

    @Query("SELECT c FROM Cart c LEFT JOIN FETCH c.cartItems WHERE c.user.id = :userId")
    Optional<Cart> findByUserIdWithCartItems(@Param("userId") Long userId);

    @Query("SELECT COUNT(ci) FROM Cart c JOIN c.cartItems ci WHERE c.user.id = :userId")
    Long countCartItemsByUserId(@Param("userId") Long userId);

    @Query("SELECT SUM(ci.quantity) FROM Cart c JOIN c.cartItems ci WHERE c.user.id = :userId")
    Long countTotalItemsByUserId(@Param("userId") Long userId);
}