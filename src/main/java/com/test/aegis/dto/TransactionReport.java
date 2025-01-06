package com.test.aegis.dto;

import java.math.BigDecimal;
import java.time.Instant;

public class TransactionReport {
    
    private BigDecimal totalPrice;
    private Long totalQuantity;
    private String createdBy;
    private Instant createdAt;

    public TransactionReport(BigDecimal totalPrice, Long totalQuantity, String createdBy, Instant createdAt) {
            this.totalPrice = totalPrice;
            this.totalQuantity = totalQuantity;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }
    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }
    public Long getTotalQuantity() {
        return totalQuantity;
    }
    public void setTotalQuantity(Long totalQuantity) {
        this.totalQuantity = totalQuantity;
    }
    public String getCreatedBy() {
        return createdBy;
    }
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
    public Instant getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public TransactionReport combine(TransactionReport other) {
        return new TransactionReport(
            this.totalPrice.add(other.totalPrice),
            this.totalQuantity + other.totalQuantity,
            this.createdBy, // Keep the same createdBy (assumes all transactions in a group have the same creator)
            this.createdAt.isBefore(other.createdAt) ? this.createdAt : other.createdAt // Earliest creation date
        );
    }

    @Override
    public String toString() {
        return "TransactionReport{" +
                "totalPrice=" + totalPrice +
                ", totalQuantity=" + totalQuantity +
                ", createdBy='" + createdBy + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
