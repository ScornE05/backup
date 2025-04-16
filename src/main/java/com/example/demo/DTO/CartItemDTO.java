package com.example.demo.DTO;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartItemDTO {
    private Long id;
    private Long cartId;

    private Long productId;
    private String productName;
    private String productImageUrl;
    private BigDecimal productPrice;
    private BigDecimal productDiscountPrice;

    private Long petId;
    private String petName;
    private String petImageUrl;
    private BigDecimal petPrice;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity = 1;

    private BigDecimal totalPrice;
}
