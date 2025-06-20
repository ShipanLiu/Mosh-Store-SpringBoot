package com.codewithmosh.store.dto.order;

import com.codewithmosh.store.dto.product.ProductSummaryDto;

import java.math.BigDecimal;

/**
 * DTO for OrderItem entity responses
 */
public class OrderItemResponseDto {
    private Long id;
    private ProductSummaryDto product;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
    
    // Constructors
    public OrderItemResponseDto() {}
    
    public OrderItemResponseDto(Long id, Integer quantity, BigDecimal unitPrice, BigDecimal totalPrice) {
        this.id = id;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.totalPrice = totalPrice;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public ProductSummaryDto getProduct() { return product; }
    public void setProduct(ProductSummaryDto product) { this.product = product; }
    
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    
    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }
    
    public BigDecimal getTotalPrice() { return totalPrice; }
    public void setTotalPrice(BigDecimal totalPrice) { this.totalPrice = totalPrice; }
} 