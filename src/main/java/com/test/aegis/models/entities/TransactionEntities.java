package com.test.aegis.models.entities;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "t_transaction")
public class TransactionEntities {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(length = 255, nullable = false, name = "TRANSACTION_CODE")
    private String transactionCode;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "transaction_product",
        joinColumns = @JoinColumn(name = "transaction_id"),
        inverseJoinColumns = @JoinColumn(name = "product_id")
    )
    private Set<ProductEntities> productEntities;

    @Column(nullable = false, name = "QUANTITY")
    private Long quantity;

    @Column(nullable = false, name = "TOTAL_PRICE")
    private BigDecimal totalPrice;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "transaction_user",
        joinColumns = @JoinColumn(name = "transaction_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<UserEntities> userEntities;

    @Column(nullable = false, name = "CREATED_BY")
    private String createdBy;

    @Column(nullable = false, name = "UPDATED_BY")
    private String updatedBy;
    
    @Column(nullable = false, name = "CREATED_AT")
    private final Instant createdAt = Instant.now();

    @Column(nullable = false, name = "MODIFIED_AT")
    private Instant modifiedAt = Instant.now();

    @PrePersist
    public void prePersist() {
        String systemUser = "system";
        if (createdBy == null) {
            createdBy = systemUser;
        }
        if (updatedBy == null) {
            updatedBy = systemUser;
        }
    }

    @PreUpdate
    public void preUpdate() {
        String systemUser = "system";
        updatedBy = systemUser;
        modifiedAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTransactionCode() {
        return transactionCode;
    }

    public void setTransactionCode(String transactionCode) {
        this.transactionCode = transactionCode;
    }

    public Set<ProductEntities> getProductEntities() {
        return productEntities;
    }

    public void setProductEntities(Set<ProductEntities> productEntities) {
        this.productEntities = productEntities;
    }

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Set<UserEntities> getUserEntities() {
        return userEntities;
    }

    public void setUserEntities(Set<UserEntities> userEntities) {
        this.userEntities = userEntities;
    }

    public void setModifiedAt(Instant modifiedAt) {
        this.modifiedAt = modifiedAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
