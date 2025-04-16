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
public class PetDTO {

    private Long id;

    private String name;

    @NotBlank(message = "Species is required")
    @Size(max = 50, message = "Species must be less than 50 characters")
    private String species;

    private String breed;

    private Integer age;

    @NotBlank(message = "Gender is required")
    @Size(max = 10, message = "Gender must be less than 10 characters")
    private String gender;

    private String color;

    private BigDecimal weight;

    private String description;

    @NotNull(message = "Price is required")
    private BigDecimal price;

    private String status = "AVAILABLE";

    private String imageUrl;

    private List<String> images = new ArrayList<>();

    private Double averageRating;

    private Long reviewCount;

    @JsonIgnore
    private LocalDateTime createdAt;

    @JsonIgnore
    private LocalDateTime updatedAt;
}