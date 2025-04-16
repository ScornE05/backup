package com.example.demo.DTO;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTO {

    private Long id;

    @NotBlank(message = "Product name is required")
    @Size(max = 100, message = "Product name must be less than 100 characters")
    private String name;

    private String description;

    @NotNull(message = "Price is required")
    private BigDecimal price;

    private BigDecimal discountPrice;

    @NotNull(message = "Quantity in stock is required")
    private Integer quantityInStock;

    @Size(max = 50, message = "SKU must be less than 50 characters")
    private String sku;

    private String imageUrl;

    @NotNull(message = "Category ID is required")
    private Long categoryId;

    private String categoryName;

    private Boolean featured = false;

    private String status = "ACTIVE";

    private List<String> images = new ArrayList<>();

    private Double averageRating;

    private Long reviewCount;

    @JsonIgnore
    private LocalDateTime createdAt;

    @JsonIgnore
    private LocalDateTime updatedAt;
}
