package com.example.demo.repository;

import com.example.demo.entity.Supplier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {

    Optional<Supplier> findByName(String name);

    Page<Supplier> findByNameContainingIgnoreCase(String name, Pageable pageable);

    @Query("SELECT DISTINCT s FROM Supplier s JOIN s.purchases p JOIN p.purchaseItems pi WHERE pi.product.id = :productId")
    List<Supplier> findSuppliersForProduct(@Param("productId") Long productId);

    @Query("SELECT s FROM Supplier s ORDER BY (SELECT COUNT(p) FROM Purchase p WHERE p.supplier.id = s.id) DESC")
    List<Supplier> findSuppliersByPurchaseCount();

    @Query("SELECT s FROM Supplier s ORDER BY (SELECT SUM(p.totalAmount) FROM Purchase p WHERE p.supplier.id = s.id) DESC")
    List<Supplier> findSuppliersByTotalPurchaseAmount();

    @Query("SELECT s FROM Supplier s WHERE s.id NOT IN (SELECT p.supplier.id FROM Purchase p WHERE p.status = 'PENDING')")
    List<Supplier> findSuppliersWithNoPendingPurchases();
}
