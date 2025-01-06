package com.test.aegis.models.entities;

import java.time.Instant;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
@Table(name = "t_user")
public class UserEntities {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "user_role",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<RoleEntities> roleEntities;

    @ManyToMany(mappedBy = "userEntities")
    @JsonIgnore
    private Set<TransactionEntities> transactionEntities;

    @Column(length = 255, nullable = false, name = "FULL_NAME")
    private String fullName;

    @Column(length = 255, nullable = false, name = "EMAIL")
    private String email;

    @Column(length = 255, nullable = false, name = "USERNAME", unique = true)
    private String username;

    @Column(length = 255, nullable = false, name = "PASSWORD")
    @JsonIgnore
    private String password;

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

    public Set<RoleEntities> getRoleEntities() {
        return roleEntities;
    }

    public void setRoleEntities(Set<RoleEntities> roleEntities) {
        this.roleEntities = roleEntities;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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
