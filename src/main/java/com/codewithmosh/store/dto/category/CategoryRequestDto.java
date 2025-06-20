package com.codewithmosh.store.dto.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO for Category creation and update requests
 */
public class CategoryRequestDto {
    
    @NotBlank(message = "Category name is required")
    @Size(max = 100, message = "Category name cannot exceed 100 characters")
    private String name;
    
    private String description;
    
    @NotBlank(message = "Category slug is required")
    @Size(max = 50, message = "Category slug cannot exceed 50 characters")
    private String slug;
    
    private Long parentId;
    
    private Boolean isActive = true;
    
    private Integer sortOrder = 0;
    
    @Size(max = 500, message = "Image URL cannot exceed 500 characters")
    private String imageUrl;
    
    // Constructors
    public CategoryRequestDto() {}
    
    public CategoryRequestDto(String name, String slug) {
        this.name = name;
        this.slug = slug;
    }
    
    public CategoryRequestDto(String name, String slug, Long parentId) {
        this.name = name;
        this.slug = slug;
        this.parentId = parentId;
    }
    
    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }
    
    public Long getParentId() { return parentId; }
    public void setParentId(Long parentId) { this.parentId = parentId; }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    
    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
    
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
} 