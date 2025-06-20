package com.codewithmosh.store.dto.product;

import com.codewithmosh.store.dto.category.CategorySummaryDto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for Product entity responses
 */
public class ProductResponseDto {
    private Long id;
    private String name;
    private String description;
    private String sku;
    private BigDecimal price;
    private Integer stockQuantity;
    private CategorySummaryDto category;
    private String imageUrl;
    private Boolean isActive;
    private Boolean isFeatured;
    private BigDecimal weight;
    private String dimensions;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean inStock;
    private boolean available;
    
    // Constructors
    public ProductResponseDto() {}
    
    public ProductResponseDto(Long id, String name, String description, String sku, 
                             BigDecimal price, Integer stockQuantity, String imageUrl,
                             Boolean isActive, Boolean isFeatured, BigDecimal weight,
                             String dimensions, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.sku = sku;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.imageUrl = imageUrl;
        this.isActive = isActive;
        this.isFeatured = isFeatured;
        this.weight = weight;
        this.dimensions = dimensions;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.inStock = stockQuantity != null && stockQuantity > 0;
        this.available = isActive != null && isActive && this.inStock;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }
    
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    
    public Integer getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(Integer stockQuantity) { 
        this.stockQuantity = stockQuantity;
        updateAvailability();
    }
    
    public CategorySummaryDto getCategory() { return category; }
    public void setCategory(CategorySummaryDto category) { this.category = category; }
    
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { 
        this.isActive = isActive;
        updateAvailability();
    }
    
    public Boolean getIsFeatured() { return isFeatured; }
    public void setIsFeatured(Boolean isFeatured) { this.isFeatured = isFeatured; }
    
    public BigDecimal getWeight() { return weight; }
    public void setWeight(BigDecimal weight) { this.weight = weight; }
    
    public String getDimensions() { return dimensions; }
    public void setDimensions(String dimensions) { this.dimensions = dimensions; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public boolean isInStock() { return inStock; }
    
    public boolean isAvailable() { return available; }
    
    private void updateAvailability() {
        this.inStock = stockQuantity != null && stockQuantity > 0;
        this.available = isActive != null && isActive && this.inStock;
    }
} 