package com.codewithmosh.store.dto.category;

/**
 * DTO for minimal Category information used in lists or as references in other DTOs
 */
public class CategorySummaryDto {
    private Long id;
    private String name;
    private String slug;
    private Boolean isActive;
    private Integer sortOrder;
    private String imageUrl;
    
    // Constructors
    public CategorySummaryDto() {}
    
    public CategorySummaryDto(Long id, String name, String slug, Boolean isActive, Integer sortOrder) {
        this.id = id;
        this.name = name;
        this.slug = slug;
        this.isActive = isActive;
        this.sortOrder = sortOrder;
    }
    
    public CategorySummaryDto(Long id, String name, String slug, Boolean isActive, 
                             Integer sortOrder, String imageUrl) {
        this.id = id;
        this.name = name;
        this.slug = slug;
        this.isActive = isActive;
        this.sortOrder = sortOrder;
        this.imageUrl = imageUrl;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    
    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
    
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
} 