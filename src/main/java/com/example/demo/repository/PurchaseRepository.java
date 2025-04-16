package com.example.demo.repository;

import com.example.demo.entity.Purchase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, Long> {

    @Query("SELECT p FROM Purchase p WHERE p.supplier.id = :supplierId")
    Page<Purchase> findBySupplierIdWithQuery(@Param("supplierId") Long supplierId, Pageable pageable);

    Page<Purchase> findByStatus(String status, Pageable pageable);

    @Query("SELECT p FROM Purchase p WHERE p.createdAt BETWEEN :startDate AND :endDate")
    List<Purchase> findByDateRange(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    @Query("SELECT p FROM Purchase p WHERE p.supplier.id = :supplierId AND p.createdAt BETWEEN :startDate AND :endDate")
    List<Purchase> findBySupplierIdAndDateRange(
            @Param("supplierId") Long supplierId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    @Query("SELECT SUM(p.totalAmount) FROM Purchase p WHERE p.status = 'COMPLETED'")
    Double getTotalPurchaseAmount();

    @Query("SELECT SUM(p.totalAmount) FROM Purchase p WHERE p.status = 'COMPLETED' AND p.createdAt BETWEEN :startDate AND :endDate")
    Double getTotalPurchaseAmountByDateRange(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    @Query("SELECT SUM(p.totalAmount) FROM Purchase p WHERE p.status = 'COMPLETED' AND p.supplier.id = :supplierId")
    Double getTotalPurchaseAmountBySupplier(@Param("supplierId") Long supplierId);

    @Query("SELECT p.supplier.id, SUM(p.totalAmount) FROM Purchase p WHERE p.status = 'COMPLETED' GROUP BY p.supplier.id ORDER BY SUM(p.totalAmount) DESC")
    List<Object[]> findTopSuppliersByAmount();
}