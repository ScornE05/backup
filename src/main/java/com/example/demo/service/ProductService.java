package com.example.demo.service;


import com.example.demo.DTO.ProductDTO;
import com.example.demo.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ProductService {

    Product createProduct(ProductDTO productDTO);

    Product updateProduct(Long id, ProductDTO productDTO);

    Optional<Product> getProductById(Long id);

    Optional<Product> getProductBySku(String sku);

    Page<Product> getAllProducts(Pageable pageable);

    Page<Product> getProductsByCategoryId(Long categoryId, Pageable pageable);

    Page<Product> searchProductsByName(String name, Pageable pageable);

    List<Product> getFeaturedProducts();

    Page<Product> getActiveProductsByCategoryId(Long categoryId, Pageable pageable);

    Page<Product> getProductsByPriceRange(Double minPrice, Double maxPrice, Pageable pageable);

    List<Product> getLowStockProducts(Integer threshold);

    void deleteProduct(Long id);

    boolean existsBySku(String sku);

    void updateProductStock(Long id, Integer quantity);
}