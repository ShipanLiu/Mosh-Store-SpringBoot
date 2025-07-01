package com.codewithmosh.store.entity.user;

import com.codewithmosh.store.entity.order.Order;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE) // the @Builder needs this AllArgsConstructor
@ToString(exclude = {"passwordHash", "roles", "orders", "addresses"})
@Table(name = "users", indexes = {
        @Index(name = "idx_user_email", columnList = "email"),
        @Index(name = "idx_user_username", columnList = "username"),
        @Index(name = "idx_user_active", columnList = "is_active")
})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false, length = 50)
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    @Setter
    private String username;
    
    @Column(unique = true, nullable = false, length = 100)
    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    @Setter
    private String email;
    
    @Column(name = "first_name", nullable = false, length = 50)
    @NotBlank(message = "First name is required")
    @Size(max = 50, message = "First name cannot exceed 50 characters")
    @Setter
    private String firstName;
    
    @Column(name = "last_name", nullable = false, length = 50)
    @NotBlank(message = "Last name is required")
    @Size(max = 50, message = "Last name cannot exceed 50 characters")
    @Setter
    private String lastName;
    
    @Column(name = "password_hash", nullable = false)
    @NotBlank(message = "Password is required")
    @Setter(AccessLevel.PRIVATE)
    private String passwordHash;
    
    @Column(length = 20)
    @Setter
    private String phone;
    
    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Relationships with proper initialization
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @Builder.Default
    private Set<UserRole> roles = new HashSet<>();
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<Order> orders = new HashSet<>();
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<UserAddress> addresses = new HashSet<>();

    // Factory method for creating new users (best practice alternative to custom constructor)
    public static User createUser(String username, String email, String firstName, String lastName, String passwordHash) {
        return User.builder()
                .username(username)
                .email(email)
                .firstName(firstName)
                .lastName(lastName)
                .passwordHash(passwordHash)
                .isActive(true)
                .build();
    }

    // Secure password handling
    public void updatePassword(String rawPassword, PasswordEncoder passwordEncoder) {
        Objects.requireNonNull(rawPassword, "Password cannot be null");
        Objects.requireNonNull(passwordEncoder, "PasswordEncoder cannot be null");
        if (rawPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }
        this.passwordHash = passwordEncoder.encode(rawPassword);
    }

    public boolean isPasswordMatch(String rawPassword, PasswordEncoder passwordEncoder) {
        Objects.requireNonNull(rawPassword, "Password cannot be null");
        Objects.requireNonNull(passwordEncoder, "PasswordEncoder cannot be null");
        return passwordEncoder.matches(rawPassword, this.passwordHash);
    }

    // Account management
    public void activate() {
        this.isActive = true;
    }

    public void deactivate() {
        this.isActive = false;
    }

    public boolean isActive() {
        return Boolean.TRUE.equals(this.isActive);
    }

    // Role management with proper bidirectional handling
    public Set<UserRole> getRoles() {
        return new HashSet<>(roles); // Defensive copy
    }

    public void addRole(UserRole role) {
        Objects.requireNonNull(role, "Role cannot be null");
        if (roles.add(role)) {
            role.setUser(this);
        }
    }

    public void removeRole(UserRole role) {
        Objects.requireNonNull(role, "Role cannot be null");
        if (roles.remove(role)) {
            role.setUser(null);
        }
    }

    public boolean hasRole(String roleName) {
        Objects.requireNonNull(roleName, "Role name cannot be null");
        return roles.stream()
                .anyMatch(role -> roleName.equals(role.getRoleName()) && 
                         Boolean.TRUE.equals(role.getIsActive()));
    }

    public boolean hasAnyRole(String... roleNames) {
        Objects.requireNonNull(roleNames, "Role names cannot be null");
        if (roleNames.length == 0) {
            return false;
        }
        return roles.stream()
                .anyMatch(role -> {
                    for (String roleName : roleNames) {
                        if (roleName != null && roleName.equals(role.getRoleName()) && 
                           Boolean.TRUE.equals(role.getIsActive())) {
                            return true;
                        }
                    }
                    return false;
                });
    }

    // Address management
    public Set<UserAddress> getAddresses() {
        return new HashSet<>(addresses); // Defensive copy
    }

    public void addAddress(UserAddress address) {
        Objects.requireNonNull(address, "Address cannot be null");
        if (addresses.add(address)) {
            address.setUser(this);
        }
    }

    public void removeAddress(UserAddress address) {
        Objects.requireNonNull(address, "Address cannot be null");
        if (addresses.remove(address)) {
            address.setUser(null);
        }
    }

    // Order management
    public Set<Order> getOrders() {
        return new HashSet<>(orders); // Defensive copy
    }

    public void addOrder(Order order) {
        Objects.requireNonNull(order, "Order cannot be null");
        if (orders.add(order)) {
            order.setUser(this);
        }
    }

    // Utility methods
    public String getFullName() {
        String first = firstName != null ? firstName.trim() : "";
        String last = lastName != null ? lastName.trim() : "";
        return (first + " " + last).trim();
    }

    public String getDisplayName() {
        String fullName = getFullName();
        return fullName.isEmpty() ? (username != null ? username : "Unknown User") : fullName;
    }

    // Lifecycle callbacks for audit
    @PrePersist
    protected void onCreate() {
        if (isActive == null) {
            isActive = true;
        }
    }

    // Override equals and hashCode for proper entity behavior
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) && 
               Objects.equals(username, user.username) && 
               Objects.equals(email, user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, email);
    }
}
