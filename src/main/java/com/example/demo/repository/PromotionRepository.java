package com.example.demo.repository;

import com.example.demo.entity.Promotion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PromotionRepository extends JpaRepository<Promotion, Long> {

    Page<Promotion> findByStatus(String status, Pageable pageable);

    @Query("SELECT p FROM Promotion p WHERE p.status = 'ACTIVE' AND p.startDate <= :now AND p.endDate >= :now")
    List<Promotion> findActivePromotions(@Param("now") LocalDateTime now);

    @Query("SELECT p FROM Promotion p WHERE p.status = 'ACTIVE' AND p.startDate <= :now AND p.endDate >= :now")
    Page<Promotion> findActivePromotions(@Param("now") LocalDateTime now, Pageable pageable);

    @Query("SELECT p FROM Promotion p WHERE p.endDate < :now")
    List<Promotion> findExpiredPromotions(@Param("now") LocalDateTime now);

    @Query("SELECT p FROM Promotion p WHERE p.startDate > :now")
    List<Promotion> findUpcomingPromotions(@Param("now") LocalDateTime now);

    @Query("SELECT DISTINCT p FROM Promotion p JOIN p.productPromotions pp WHERE pp.product.id = :productId AND p.status = 'ACTIVE' AND p.startDate <= :now AND p.endDate >= :now")
    List<Promotion> findActivePromotionsByProductId(@Param("productId") Long productId, @Param("now") LocalDateTime now);

    @Query("SELECT DISTINCT p FROM Promotion p JOIN p.categoryPromotions cp WHERE cp.category.id = :categoryId AND p.status = 'ACTIVE' AND p.startDate <= :now AND p.endDate >= :now")
    List<Promotion> findActivePromotionsByCategoryId(@Param("categoryId") Long categoryId, @Param("now") LocalDateTime now);
}