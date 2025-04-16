package com.example.demo.repository;

import com.example.demo.entity.Wishlist;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, Long> {

    List<Wishlist> findByUserId(Long userId);

    Page<Wishlist> findByUserId(Long userId, Pageable pageable);

    Optional<Wishlist> findByUserIdAndProductId(Long userId, Long productId);

    Optional<Wishlist> findByUserIdAndPetId(Long userId, Long petId);

    @Query("SELECT CASE WHEN COUNT(w) > 0 THEN true ELSE false END FROM Wishlist w WHERE w.user.id = :userId AND w.product.id = :productId")
    boolean existsByUserIdAndProductId(@Param("userId") Long userId, @Param("productId") Long productId);

    @Query("SELECT CASE WHEN COUNT(w) > 0 THEN true ELSE false END FROM Wishlist w WHERE w.user.id = :userId AND w.pet.id = :petId")
    boolean existsByUserIdAndPetId(@Param("userId") Long userId, @Param("petId") Long petId);

    @Query("SELECT COUNT(w) FROM Wishlist w WHERE w.product.id = :productId")
    Long countByProductId(@Param("productId") Long productId);

    @Query("SELECT COUNT(w) FROM Wishlist w WHERE w.pet.id = :petId")
    Long countByPetId(@Param("petId") Long petId);
}