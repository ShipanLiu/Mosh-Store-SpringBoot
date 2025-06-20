package com.codewithmosh.store.dto.product;

import java.math.BigDecimal;

/**
 * DTO for minimal Product information used in lists or as references in other DTOs
 */
public class ProductSummaryDto {
    private Long id;
    private String name;
    private String sku;
    private BigDecimal price;
    private Integer stockQuantity;
    private String imageUrl;
    private Boolean isActive;
    private Boolean isFeatured;
    private boolean inStock;
    private boolean available;
    
    // Constructors
    public ProductSummaryDto() {}
    
    public ProductSummaryDto(Long id, String name, String sku, BigDecimal price, 
                            Integer stockQuantity, String imageUrl, Boolean isActive, Boolean isFeatured) {
        this.id = id;
        this.name = name;
        this.sku = sku;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.imageUrl = imageUrl;
        this.isActive = isActive;
        this.isFeatured = isFeatured;
        this.inStock = stockQuantity != null && stockQuantity > 0;
        this.available = isActive != null && isActive && this.inStock;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }
    
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    
    public Integer getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(Integer stockQuantity) { 
        this.stockQuantity = stockQuantity;
        updateAvailability();
    }
    
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { 
        this.isActive = isActive;
        updateAvailability();
    }
    
    public Boolean getIsFeatured() { return isFeatured; }
    public void setIsFeatured(Boolean isFeatured) { this.isFeatured = isFeatured; }
    
    public boolean isInStock() { return inStock; }
    
    public boolean isAvailable() { return available; }
    
    private void updateAvailability() {
        this.inStock = stockQuantity != null && stockQuantity > 0;
        this.available = isActive != null && isActive && this.inStock;
    }
} 