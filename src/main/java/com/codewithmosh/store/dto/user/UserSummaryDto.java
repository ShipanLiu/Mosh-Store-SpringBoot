package com.codewithmosh.store.dto.user;

/**
 * DTO for minimal User information used in lists or as references in other DTOs
 */
public class UserSummaryDto {
    private Long id;
    private String username;
    private String fullName;
    private String email;
    private Boolean isActive;
    
    // Constructors
    public UserSummaryDto() {}
    
    public UserSummaryDto(Long id, String username, String fullName, String email, Boolean isActive) {
        this.id = id;
        this.username = username;
        this.fullName = fullName;
        this.email = email;
        this.isActive = isActive;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
} 