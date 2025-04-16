package com.example.demo.service.impl;

import com.example.demo.DTO.ProductDTO;
import com.example.demo.entity.Category;
import com.example.demo.entity.Product;
import com.example.demo.entity.ProductImage;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.ProductImageRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.service.ProductService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductImageRepository productImageRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository,
                              CategoryRepository categoryRepository,
                              ProductImageRepository productImageRepository,
                              ModelMapper modelMapper) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.productImageRepository = productImageRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    @Transactional
    public Product createProduct(ProductDTO productDTO) {
        Category category = categoryRepository.findById(productDTO.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + productDTO.getCategoryId()));

        Product product = new Product();
        product.setName(productDTO.getName());
        product.setDescription(productDTO.getDescription());
        product.setPrice(productDTO.getPrice());
        product.setDiscountPrice(productDTO.getDiscountPrice());
        product.setQuantityInStock(productDTO.getQuantityInStock());
        product.setSku(productDTO.getSku());
        product.setImageUrl(productDTO.getImageUrl());
        product.setCategory(category);
        product.setFeatured(productDTO.getFeatured() != null ? productDTO.getFeatured() : false);
        product.setStatus(productDTO.getStatus() != null ? productDTO.getStatus() : "ACTIVE");
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());

        product = productRepository.save(product);

        // Add product images if provided
        if (productDTO.getImages() != null && !productDTO.getImages().isEmpty()) {
            for (int i = 0; i < productDTO.getImages().size(); i++) {
                ProductImage image = new ProductImage();
                image.setProduct(product);
                image.setImageUrl(productDTO.getImages().get(i));
                image.setIsPrimary(i == 0); // First image is primary
                productImageRepository.save(image);
            }
        }

        return product;
    }

    @Override
    @Transactional
    public Product updateProduct(Long id, ProductDTO productDTO) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        if (productDTO.getCategoryId() != null && !productDTO.getCategoryId().equals(product.getCategory().getId())) {
            Category category = categoryRepository.findById(productDTO.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + productDTO.getCategoryId()));
            product.setCategory(category);
        }

        if (productDTO.getName() != null) {
            product.setName(productDTO.getName());
        }

        if (productDTO.getDescription() != null) {
            product.setDescription(productDTO.getDescription());
        }

        if (productDTO.getPrice() != null) {
            product.setPrice(productDTO.getPrice());
        }

        if (productDTO.getDiscountPrice() != null) {
            product.setDiscountPrice(productDTO.getDiscountPrice());
        }

        if (productDTO.getQuantityInStock() != null) {
            product.setQuantityInStock(productDTO.getQuantityInStock());
        }

        if (productDTO.getSku() != null) {
            product.setSku(productDTO.getSku());
        }

        if (productDTO.getImageUrl() != null) {
            product.setImageUrl(productDTO.getImageUrl());
        }

        if (productDTO.getFeatured() != null) {
            product.setFeatured(productDTO.getFeatured());
        }

        if (productDTO.getStatus() != null) {
            product.setStatus(productDTO.getStatus());
        }

        product.setUpdatedAt(LocalDateTime.now());

        product = productRepository.save(product);

        // Update product images if provided
        if (productDTO.getImages() != null && !productDTO.getImages().isEmpty()) {
            // Delete existing images
            productImageRepository.deleteByProductId(id);

            // Add new images
            for (int i = 0; i < productDTO.getImages().size(); i++) {
                ProductImage image = new ProductImage();
                image.setProduct(product);
                image.setImageUrl(productDTO.getImages().get(i));
                image.setIsPrimary(i == 0); // First image is primary
                productImageRepository.save(image);
            }
        }

        return product;
    }

    @Override
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    @Override
    public Optional<Product> getProductBySku(String sku) {
        return productRepository.findBySku(sku);
    }

    @Override
    public Page<Product> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    @Override
    public Page<Product> getProductsByCategoryId(Long categoryId, Pageable pageable) {
        return productRepository.findByCategoryId(categoryId, pageable);
    }

    @Override
    public Page<Product> searchProductsByName(String name, Pageable pageable) {
        return productRepository.findByNameContainingIgnoreCase(name, pageable);
    }

    @Override
    public List<Product> getFeaturedProducts() {
        return productRepository.findFeaturedProducts();
    }

    @Override
    public Page<Product> getActiveProductsByCategoryId(Long categoryId, Pageable pageable) {
        return productRepository.findActiveByCategoryId(categoryId, pageable);
    }

    @Override
    public Page<Product> getProductsByPriceRange(Double minPrice, Double maxPrice, Pageable pageable) {
        return productRepository.findByPriceRange(minPrice, maxPrice, pageable);
    }

    @Override
    public List<Product> getLowStockProducts(Integer threshold) {
        return productRepository.findLowStockProducts(threshold);
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    @Override
    public boolean existsBySku(String sku) {
        return productRepository.findBySku(sku).isPresent();
    }

    @Override
    @Transactional
    public void updateProductStock(Long id, Integer quantity) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        product.setQuantityInStock(product.getQuantityInStock() + quantity);
        product.setUpdatedAt(LocalDateTime.now());

        productRepository.save(product);
    }
}