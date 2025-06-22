package com.codewithmosh.store.mapper;

import com.codewithmosh.store.dto.category.CategoryResponseDto;
import com.codewithmosh.store.dto.category.CategorySummaryDto;
import com.codewithmosh.store.dto.order.OrderResponseDto;
import com.codewithmosh.store.dto.order.OrderItemResponseDto;
import com.codewithmosh.store.dto.order.PaymentSummaryDto;
import com.codewithmosh.store.dto.product.ProductResponseDto;
import com.codewithmosh.store.dto.product.ProductSummaryDto;
import com.codewithmosh.store.dto.user.UserResponseDto;
import com.codewithmosh.store.dto.user.UserSummaryDto;
import com.codewithmosh.store.dto.payment.PaymentResponseDto;
import com.codewithmosh.store.entity.order.Order;
import com.codewithmosh.store.entity.order.OrderItem;
import com.codewithmosh.store.entity.payment.Payment;
import com.codewithmosh.store.entity.product.Category;
import com.codewithmosh.store.entity.product.Product;
import com.codewithmosh.store.entity.user.User;
import com.codewithmosh.store.entity.user.UserRole;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Utility class for mapping between entities and DTOs
 * Follows best practices for clean separation between data layers
 */
@Component
public class EntityDtoMapper {
    
    // User mappings
    public UserResponseDto toUserResponseDto(User user) {
        if (user == null) return null;
        
        UserResponseDto dto = new UserResponseDto(
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            user.getFirstName(),
            user.getLastName(),
            user.getPhone(),
            user.getIsActive(),
            user.getCreatedAt(),
            user.getUpdatedAt()
        );
        
        // Map roles to simple strings
        if (user.getRoles() != null) {
            Set<String> roleNames = user.getRoles().stream()
                .filter(role -> role.getIsActive())
                .map(UserRole::getRoleName)
                .collect(Collectors.toSet());
            dto.setRoles(roleNames);
        }
        
        return dto;
    }
    
    public UserSummaryDto toUserSummaryDto(User user) {
        if (user == null) return null;
        
        return new UserSummaryDto(
            user.getId(),
            user.getUsername(),
            user.getFullName(),
            user.getEmail(),
            user.getIsActive()
        );
    }
    
    // Product mappings
    public ProductResponseDto toProductResponseDto(Product product) {
        if (product == null) return null;
        
        ProductResponseDto dto = new ProductResponseDto(
            product.getId(),
            product.getName(),
            product.getDescription(),
            product.getSku(),
            product.getPrice(),
            product.getStockQuantity(),
            product.getImageUrl(),
            product.getIsActive(),
            product.getIsFeatured(),
            product.getWeight(),
            product.getDimensions(),
            product.getCreatedAt(),
            product.getUpdatedAt()
        );
        
        // Map category
        if (product.getCategory() != null) {
            dto.setCategory(toCategorySummaryDto(product.getCategory()));
        }
        
        return dto;
    }
    
    public ProductSummaryDto toProductSummaryDto(Product product) {
        if (product == null) return null;
        
        return new ProductSummaryDto(
            product.getId(),
            product.getName(),
            product.getSku(),
            product.getPrice(),
            product.getStockQuantity(),
            product.getImageUrl(),
            product.getIsActive(),
            product.getIsFeatured()
        );
    }
    
    // Category mappings
    public CategoryResponseDto toCategoryResponseDto(Category category) {
        if (category == null) return null;
        
        CategoryResponseDto dto = new CategoryResponseDto(
            category.getId(),
            category.getName(),
            category.getDescription(),
            category.getSlug(),
            category.getIsActive(),
            category.getSortOrder(),
            category.getImageUrl(),
            category.getCreatedAt(),
            category.getUpdatedAt()
        );
        
        // Map parent category
        if (category.getParent() != null) {
            dto.setParent(toCategorySummaryDto(category.getParent()));
        }
        
        // Map subcategories
        if (category.getSubcategories() != null && !category.getSubcategories().isEmpty()) {
            List<CategorySummaryDto> subcategories = category.getSubcategories().stream()
                .map(this::toCategorySummaryDto)
                .collect(Collectors.toList());
            dto.setSubcategories(subcategories);
        }
        
        // Map products
        if (category.getProducts() != null && !category.getProducts().isEmpty()) {
            List<ProductSummaryDto> products = category.getProducts().stream()
                .filter(product -> product.getIsActive())
                .map(this::toProductSummaryDto)
                .collect(Collectors.toList());
            dto.setProducts(products);
        }
        
        return dto;
    }
    
    public CategorySummaryDto toCategorySummaryDto(Category category) {
        if (category == null) return null;
        
        return new CategorySummaryDto(
            category.getId(),
            category.getName(),
            category.getSlug(),
            category.getIsActive(),
            category.getSortOrder(),
            category.getImageUrl()
        );
    }
    
    // Order mappings
    public OrderResponseDto toOrderResponseDto(Order order) {
        if (order == null) return null;
        
        OrderResponseDto dto = new OrderResponseDto(
            order.getId(),
            order.getOrderNumber(),
            order.getTotalAmount(),
            order.getStatus(),
            order.getOrderDate(),
            order.getCreatedAt(),
            order.getUpdatedAt()
        );
        
        // Map user
        if (order.getUser() != null) {
            dto.setUser(toUserSummaryDto(order.getUser()));
        }
        
        // Map addresses
        dto.setShippingAddress(order.getShippingAddress());
        dto.setBillingAddress(order.getBillingAddress());
        dto.setShippedDate(order.getShippedDate());
        dto.setDeliveredDate(order.getDeliveredDate());
        
        // Map order items
        if (order.getOrderItems() != null && !order.getOrderItems().isEmpty()) {
            List<OrderItemResponseDto> orderItems = order.getOrderItems().stream()
                .map(this::toOrderItemResponseDto)
                .collect(Collectors.toList());
            dto.setOrderItems(orderItems);
        }
        
        // Map payments
        if (order.getPayments() != null && !order.getPayments().isEmpty()) {
            List<PaymentSummaryDto> payments = order.getPayments().stream()
                .map(this::toPaymentSummaryDto)
                .collect(Collectors.toList());
            dto.setPayments(payments);
        }
        
        return dto;
    }
    
    public OrderItemResponseDto toOrderItemResponseDto(OrderItem orderItem) {
        if (orderItem == null) return null;
        
        OrderItemResponseDto dto = new OrderItemResponseDto(
            orderItem.getId(),
            orderItem.getQuantity(),
            orderItem.getUnitPrice(),
            orderItem.getTotalPrice()
        );
        
        // Map product
        if (orderItem.getProduct() != null) {
            dto.setProduct(toProductSummaryDto(orderItem.getProduct()));
        }
        
        return dto;
    }
    
    // Payment mappings
    public PaymentResponseDto toPaymentResponseDto(Payment payment) {
        if (payment == null) return null;
        
        PaymentResponseDto dto = new PaymentResponseDto(
            payment.getId(),
            payment.getOrder() != null ? payment.getOrder().getId() : null,
            payment.getAmount(),
            payment.getPaymentMethod(),
            payment.getStatus(),
            payment.getTransactionId(),
            payment.getPaymentDate(),
            payment.getCreatedAt(),
            payment.getUpdatedAt()
        );
        
        dto.setProcessorResponse(payment.getPaymentDetails());
        dto.setExternalPaymentId(payment.getExternalPaymentId());
        dto.setFailureReason(payment.getFailureReason());
        dto.setRefundAmount(payment.getRefundAmount());
        dto.setRefundDate(payment.getRefundDate());
        
        return dto;
    }
    
    public PaymentSummaryDto toPaymentSummaryDto(Payment payment) {
        if (payment == null) return null;
        
        return new PaymentSummaryDto(
            payment.getId(),
            payment.getAmount(),
            payment.getPaymentMethod(),
            payment.getStatus(),
            payment.getTransactionId(),
            payment.getPaymentDate()
        );
    }
} 