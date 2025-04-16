package com.example.demo.repository;

import com.example.demo.entity.ProductPromotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductPromotionRepository extends JpaRepository<ProductPromotion, Long> {

    List<ProductPromotion> findByProductId(Long productId);

    List<ProductPromotion> findByPromotionId(Long promotionId);

    @Query("SELECT pp FROM ProductPromotion pp WHERE pp.promotion.id = :promotionId AND pp.product.id = :productId")
    ProductPromotion findByPromotionIdAndProductId(@Param("promotionId") Long promotionId, @Param("productId") Long productId);

    @Modifying
    @Query("DELETE FROM ProductPromotion pp WHERE pp.promotion.id = :promotionId")
    void deleteByPromotionId(@Param("promotionId") Long promotionId);

    @Modifying
    @Query("DELETE FROM ProductPromotion pp WHERE pp.product.id = :productId")
    void deleteByProductId(@Param("productId") Long productId);

    @Query("SELECT CASE WHEN COUNT(pp) > 0 THEN true ELSE false END FROM ProductPromotion pp WHERE pp.promotion.id = :promotionId AND pp.product.id = :productId")
    boolean existsByPromotionIdAndProductId(@Param("promotionId") Long promotionId, @Param("productId") Long productId);
}
