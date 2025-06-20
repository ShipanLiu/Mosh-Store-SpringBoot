package com.codewithmosh.store.dto.category;

import com.codewithmosh.store.dto.product.ProductSummaryDto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for Category entity responses
 */
public class CategoryResponseDto {
    private Long id;
    private String name;
    private String description;
    private String slug;
    private CategorySummaryDto parent;
    private Boolean isActive;
    private Integer sortOrder;
    private String imageUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<CategorySummaryDto> subcategories;
    private List<ProductSummaryDto> products;
    private boolean hasParent;
    private boolean hasSubcategories;
    private boolean hasProducts;
    
    // Constructors
    public CategoryResponseDto() {}
    
    public CategoryResponseDto(Long id, String name, String description, String slug,
                              Boolean isActive, Integer sortOrder, String imageUrl,
                              LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.slug = slug;
        this.isActive = isActive;
        this.sortOrder = sortOrder;
        this.imageUrl = imageUrl;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }
    
    public CategorySummaryDto getParent() { return parent; }
    public void setParent(CategorySummaryDto parent) { 
        this.parent = parent;
        this.hasParent = parent != null;
    }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    
    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
    
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public List<CategorySummaryDto> getSubcategories() { return subcategories; }
    public void setSubcategories(List<CategorySummaryDto> subcategories) { 
        this.subcategories = subcategories;
        this.hasSubcategories = subcategories != null && !subcategories.isEmpty();
    }
    
    public List<ProductSummaryDto> getProducts() { return products; }
    public void setProducts(List<ProductSummaryDto> products) { 
        this.products = products;
        this.hasProducts = products != null && !products.isEmpty();
    }
    
    public boolean isHasParent() { return hasParent; }
    
    public boolean isHasSubcategories() { return hasSubcategories; }
    
    public boolean isHasProducts() { return hasProducts; }
} 