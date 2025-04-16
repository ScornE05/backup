package com.example.demo.repository;

import com.example.demo.entity.CategoryPromotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryPromotionRepository extends JpaRepository<CategoryPromotion, Long> {

    List<CategoryPromotion> findByCategoryId(Long categoryId);

    List<CategoryPromotion> findByPromotionId(Long promotionId);

    @Query("SELECT cp FROM CategoryPromotion cp WHERE cp.promotion.id = :promotionId AND cp.category.id = :categoryId")
    CategoryPromotion findByPromotionIdAndCategoryId(@Param("promotionId") Long promotionId, @Param("categoryId") Long categoryId);

    @Modifying
    @Query("DELETE FROM CategoryPromotion cp WHERE cp.promotion.id = :promotionId")
    void deleteByPromotionId(@Param("promotionId") Long promotionId);

    @Modifying
    @Query("DELETE FROM CategoryPromotion cp WHERE cp.category.id = :categoryId")
    void deleteByCategoryId(@Param("categoryId") Long categoryId);

    @Query("SELECT CASE WHEN COUNT(cp) > 0 THEN true ELSE false END FROM CategoryPromotion cp WHERE cp.promotion.id = :promotionId AND cp.category.id = :categoryId")
    boolean existsByPromotionIdAndCategoryId(@Param("promotionId") Long promotionId, @Param("categoryId") Long categoryId);
}
