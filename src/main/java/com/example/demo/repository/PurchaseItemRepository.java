package com.example.demo.repository;

import com.example.demo.entity.PurchaseItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PurchaseItemRepository extends JpaRepository<PurchaseItem, Long> {

    List<PurchaseItem> findByPurchaseId(Long purchaseId);

    @Query("SELECT pi FROM PurchaseItem pi WHERE pi.product.id = :productId")
    List<PurchaseItem> findByProductId(@Param("productId") Long productId);

    @Query("SELECT pi FROM PurchaseItem pi WHERE pi.pet.id = :petId")
    List<PurchaseItem> findByPetId(@Param("petId") Long petId);

    @Query("SELECT SUM(pi.quantity) FROM PurchaseItem pi WHERE pi.product.id = :productId")
    Long getTotalPurchasedQuantityByProductId(@Param("productId") Long productId);

    @Query("SELECT pi.product.id, SUM(pi.quantity) FROM PurchaseItem pi WHERE pi.product.id IS NOT NULL GROUP BY pi.product.id ORDER BY SUM(pi.quantity) DESC")
    List<Object[]> findTopPurchasedProducts();

    @Query("SELECT pi.product.id, AVG(pi.unitCost) FROM PurchaseItem pi WHERE pi.product.id IS NOT NULL GROUP BY pi.product.id")
    List<Object[]> getAveragePurchaseCostByProduct();

    @Query("SELECT pi FROM PurchaseItem pi WHERE pi.purchase.supplier.id = :supplierId AND pi.product.id = :productId")
    List<PurchaseItem> findBySupplierIdAndProductId(@Param("supplierId") Long supplierId, @Param("productId") Long productId);
}
