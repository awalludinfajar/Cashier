package com.test.aegis.models.entities;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "t_product")
public class ProductEntities {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToMany(mappedBy = "productEntities")
    @JsonIgnore
    private Set<TransactionEntities> transactionEntities;

    @Column(length = 255, nullable = false, name = "NAME")
    private String name;

    @Column(length = 255, nullable = false, name = "CATEGORY")
    private String category;

    @Column(nullable = false, name = "PRICE")
    private BigDecimal price;

    @Column(nullable = false, name = "QUANTITY")
    private Integer quantity;

    @Column(length = 500, name = "DESCRIPTION")
    private String description;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setModifiedAt(Instant modifiedAt) {
        this.modifiedAt = (modifiedAt != null) ? modifiedAt : Instant.now();
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
}