package com.codewithmosh.store.controller;

import com.codewithmosh.store.dto.common.ApiResponse;
import com.codewithmosh.store.dto.user.UserDto;
import com.codewithmosh.store.dto.user.UserRequestDto;
import com.codewithmosh.store.dto.user.UserResponseDto;
import com.codewithmosh.store.entity.user.User;
import com.codewithmosh.store.mapper.EntityDtoMapper;
import com.codewithmosh.store.service.notification.NotificationService;
import com.codewithmosh.store.service.order.OrderService;
import com.codewithmosh.store.service.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Home Controller - Provides basic application endpoints and demo functionality
 * 
 * This controller serves as the main entry point for the application and provides
 * demo endpoints for testing various services and integrations.
 * 
 * @author Store Application Team
 * @version 1.0
 * @since 1.0
 */
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Slf4j
@Validated
@Tag(name = "Home", description = "Home and demo endpoints")
public class HomeController {

    @Value("${spring.application.name:Store Application}")
    private String applicationName;

    @Value("${spring.application.version:1.0.0}")
    private String applicationVersion;

    private final OrderService orderService;
    private final NotificationService notificationService;
    private final UserService userService;
    private final EntityDtoMapper entityDtoMapper;
    private final PasswordEncoder passwordEncoder; // I have defined this bean in the SecurityConfig

    /**
     * Application health check and basic information endpoint
     */
    @Operation(
        summary = "Get application information",
        description = "Returns basic application information including name, version, and status"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", 
            description = "Application information retrieved successfully",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        )
    })
    @GetMapping("/")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getApplicationInfo() {
        log.info("Application info requested");
        
        Map<String, Object> appInfo = new HashMap<>();
        appInfo.put("name", applicationName);
        appInfo.put("version", applicationVersion);
        appInfo.put("status", "running");
        appInfo.put("description", "Modern Spring Boot e-commerce application");
        
        return ResponseEntity.ok(ApiResponse.success("Application is running", appInfo));
    }

    /**
     * Health check endpoint
     */
    @Operation(
        summary = "Health check",
        description = "Simple health check endpoint to verify application status"
    )
    @GetMapping("/health")
    public ResponseEntity<ApiResponse<String>> health() {
        return ResponseEntity.ok(ApiResponse.success("Application is healthy"));
    }

    /**
     * Demo endpoint for order placement (for testing purposes)
     */
    @Operation(
        summary = "Demo order placement",
        description = "Demo endpoint to test order service integration. For development/testing only."
    )
    @PostMapping("/demo/order")
    public ResponseEntity<ApiResponse<String>> demoOrderPlacement(
            @Parameter(description = "Order amount for demo", example = "299.99")
            @RequestParam(defaultValue = "100.0") Double amount) {
        
        try {
            log.info("Demo order placement requested with amount: {}", amount);
            
            if (amount <= 0) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Amount must be greater than 0"));
            }
            
            orderService.placeOrder(amount);
            
            return ResponseEntity.ok(
                ApiResponse.success("Demo order placed successfully with amount: $" + amount)
            );
            
        } catch (Exception e) {
            log.error("Error in demo order placement", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to place demo order: " + e.getMessage()));
        }
    }

    /**
     * Demo endpoint for notification sending
     */
    @Operation(
        summary = "Send demo notification",
        description = "Demo endpoint to test notification service integration"
    )
    @PostMapping("/demo/notification")
    public ResponseEntity<ApiResponse<String>> sendDemoNotification(
            @Parameter(description = "Notification message", example = "Hello, this is a test notification")
            @RequestParam @NotBlank(message = "Message cannot be blank") String message) {
        
        try {
            log.info("Demo notification requested with message: {}", message);
            
            notificationService.sendNotification(message);
            
            return ResponseEntity.ok(
                ApiResponse.success("Notification sent successfully: " + message)
            );
            
        } catch (Exception e) {
            log.error("Error sending demo notification", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to send notification: " + e.getMessage()));
        }
    }

    /**
     * Demo endpoint for user registration (for testing purposes)
     */
    @Operation(
        summary = "Demo user registration",
        description = "Demo endpoint to test user registration. Creates a user with provided details."
    )
    @PostMapping("/demo/register")
    public ResponseEntity<ApiResponse<UserResponseDto>> demoUserRegistration(
            @Valid @RequestBody UserRequestDto userRequest,
            HttpServletRequest request) {
        
        try {
            log.info("Demo user registration requested for username: {}", userRequest.getUsername());
            
            // Create user using the factory method from User entity
            User newUser = User.createUser(
                userRequest.getUsername(),
                userRequest.getEmail(),
                userRequest.getFirstName(),
                userRequest.getLastName(),
                passwordEncoder.encode(userRequest.getPassword())
            );
            
            // Set optional fields
            if (userRequest.getPhone() != null) {
                newUser.setPhone(userRequest.getPhone());
            }
            
            // Register user through service
            User savedUser = userService.register(newUser);
            
            // Convert to response DTO
            UserResponseDto responseDto = entityDtoMapper.toUserResponseDto(savedUser);
            
            // Set path for response
            ApiResponse<UserResponseDto> response = ApiResponse.success(
                "User registered successfully", responseDto
            );
            response.setPath(request.getRequestURI());
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (IllegalArgumentException e) {
            log.warn("Invalid user registration request: {}", e.getMessage());
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Registration failed: " + e.getMessage()));
                
        } catch (Exception e) {
            log.error("Error during demo user registration", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Registration failed due to internal error"));
        }
    }

    /**
     * Demo endpoint to get sample user information
     */
    @Operation(
        summary = "Get sample user information",
        description = "Returns sample user data for demo purposes. Does not interact with database."
    )
    @GetMapping("/demo/user-info")
    public ResponseEntity<ApiResponse<UserDto>> getSampleUserInfo() {
        log.info("Sample user info requested");
        
        // Create sample user data using builder pattern
        UserDto sampleUser = UserDto.builder()
            .id(999L)
            .username("demo_user")
            .email("demo@example.com")
            .firstName("Demo")
            .lastName("User")
            .phone("+1-555-0123")
            .isActive(true)
            .fullName("Demo User")
            .displayName("Demo User")
            .totalOrders(5)
            .totalAddresses(2)
            .build();
        
        return ResponseEntity.ok(
            ApiResponse.success("Sample user information", sampleUser)
        );
    }

    /**
     * Demo endpoint to get application statistics
     */
    @Operation(
        summary = "Get application statistics",
        description = "Returns basic application statistics for demo purposes"
    )
    @GetMapping("/demo/stats")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getApplicationStats() {
        log.info("Application stats requested");
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalEndpoints", 6);
        stats.put("version", applicationVersion);
        stats.put("uptime", "Demo mode");
        stats.put("environment", "development");
        stats.put("features", new String[]{"User Management", "Order Processing", "Notifications"});
        
        return ResponseEntity.ok(
            ApiResponse.success("Application statistics", stats)
        );
    }
}
