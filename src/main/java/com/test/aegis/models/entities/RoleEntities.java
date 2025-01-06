package com.test.aegis.models.entities;

import java.io.Serializable;
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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "t_role")
public class RoleEntities implements Serializable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToMany(mappedBy = "roleEntities")
    @JsonIgnore
    private Set<UserEntities> userEntities;

    @Column(length = 255, nullable = false, name = "NAME", unique = true)
    private String name;

    @Column(length = 255, nullable = true, name = "DESCRIPTION")
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

    public Set<UserEntities> getUserEntities() {
        return userEntities;
    }

    public void setUserEntities(Set<UserEntities> userEntities) {
        this.userEntities = userEntities;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
