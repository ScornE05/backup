package com.example.demo.Controller;


import com.example.demo.DTO.ApiResponse;
import com.example.demo.DTO.ProductDTO;
import com.example.demo.entity.Product;
import com.example.demo.entity.ProductImage;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.ProductImageRepository;
import com.example.demo.repository.ReviewRepository;
import com.example.demo.service.ProductService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;
    private final ProductImageRepository productImageRepository;
    private final ReviewRepository reviewRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public ProductController(ProductService productService,
                             ProductImageRepository productImageRepository,
                             ReviewRepository reviewRepository,
                             ModelMapper modelMapper) {
        this.productService = productService;
        this.productImageRepository = productImageRepository;
        this.reviewRepository = reviewRepository;
        this.modelMapper = modelMapper;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductDTO> createProduct(@Valid @RequestBody ProductDTO productDTO) {
        // Check if SKU already exists
        if (productDTO.getSku() != null && !productDTO.getSku().isEmpty() && productService.existsBySku(productDTO.getSku())) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Product product = productService.createProduct(productDTO);
        ProductDTO responseDTO = convertToDto(product);

        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable Long id) {
        Product product = productService.getProductById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        ProductDTO productDTO = convertToDto(product);

        // Get average rating and review count
        Double averageRating = reviewRepository.getAverageRatingByProductId(id);
        Long reviewCount = reviewRepository.countByProductId(id);

        productDTO.setAverageRating(averageRating);
        productDTO.setReviewCount(reviewCount);

        return ResponseEntity.ok(productDTO);
    }

    @GetMapping
    public ResponseEntity<Page<ProductDTO>> getAllProducts(Pageable pageable) {
        Page<Product> products = productService.getAllProducts(pageable);
        Page<ProductDTO> productDTOs = products.map(this::convertToDto);

        return ResponseEntity.ok(productDTOs);
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<Page<ProductDTO>> getProductsByCategoryId(@PathVariable Long categoryId, Pageable pageable) {
        Page<Product> products = productService.getProductsByCategoryId(categoryId, pageable);
        Page<ProductDTO> productDTOs = products.map(this::convertToDto);

        return ResponseEntity.ok(productDTOs);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<ProductDTO>> searchProductsByName(@RequestParam String name, Pageable pageable) {
        Page<Product> products = productService.searchProductsByName(name, pageable);
        Page<ProductDTO> productDTOs = products.map(this::convertToDto);

        return ResponseEntity.ok(productDTOs);
    }

    @GetMapping("/featured")
    public ResponseEntity<List<ProductDTO>> getFeaturedProducts() {
        List<Product> products = productService.getFeaturedProducts();
        List<ProductDTO> productDTOs = products.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(productDTOs);
    }

    @GetMapping("/active/category/{categoryId}")
    public ResponseEntity<Page<ProductDTO>> getActiveProductsByCategoryId(@PathVariable Long categoryId, Pageable pageable) {
        Page<Product> products = productService.getActiveProductsByCategoryId(categoryId, pageable);
        Page<ProductDTO> productDTOs = products.map(this::convertToDto);

        return ResponseEntity.ok(productDTOs);
    }

    @GetMapping("/price-range")
    public ResponseEntity<Page<ProductDTO>> getProductsByPriceRange(
            @RequestParam Double minPrice,
            @RequestParam Double maxPrice,
            Pageable pageable) {
        Page<Product> products = productService.getProductsByPriceRange(minPrice, maxPrice, pageable);
        Page<ProductDTO> productDTOs = products.map(this::convertToDto);

        return ResponseEntity.ok(productDTOs);
    }

    @GetMapping("/low-stock")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    public ResponseEntity<List<ProductDTO>> getLowStockProducts(@RequestParam(defaultValue = "10") Integer threshold) {
        List<Product> products = productService.getLowStockProducts(threshold);
        List<ProductDTO> productDTOs = products.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(productDTOs);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductDTO productDTO) {
        // Check if SKU already exists (excluding the current product)
        if (productDTO.getSku() != null && !productDTO.getSku().isEmpty()) {
            productService.getProductBySku(productDTO.getSku())
                    .ifPresent(product -> {
                        if (!product.getId().equals(id)) {
                            throw new IllegalArgumentException("SKU already exists");
                        }
                    });
        }

        Product updatedProduct = productService.updateProduct(id, productDTO);
        ProductDTO responseDTO = convertToDto(updatedProduct);

        return ResponseEntity.ok(responseDTO);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok(new ApiResponse(true, "Product deleted successfully"));
    }

    @PutMapping("/{id}/stock")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    public ResponseEntity<ApiResponse> updateProductStock(
            @PathVariable Long id,
            @RequestParam Integer quantity) {
        productService.updateProductStock(id, quantity);
        return ResponseEntity.ok(new ApiResponse(true, "Product stock updated successfully"));
    }

    private ProductDTO convertToDto(Product product) {
        ProductDTO productDTO = modelMapper.map(product, ProductDTO.class);

        // Set category information
        if (product.getCategory() != null) {
            productDTO.setCategoryId(product.getCategory().getId());
            productDTO.setCategoryName(product.getCategory().getName());
        }

        // Set price values
        productDTO.setPrice(product.getPrice());
        if (product.getDiscountPrice() != null) {
            productDTO.setDiscountPrice(product.getDiscountPrice());
        }

        // Set images
        List<ProductImage> images = productImageRepository.findByProductId(product.getId());
        if (images != null && !images.isEmpty()) {
            productDTO.setImages(images.stream()
                    .map(ProductImage::getImageUrl)
                    .collect(Collectors.toList()));
        }

        return productDTO;
    }
}
