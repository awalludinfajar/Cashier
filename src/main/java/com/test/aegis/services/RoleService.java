package com.test.aegis.services;

import java.util.List;
import java.util.Optional;

import org.apache.coyote.BadRequestException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.test.aegis.dto.RoleData;
import com.test.aegis.models.entities.RoleEntities;
import com.test.aegis.models.entities.UserEntities;
import com.test.aegis.models.repos.RoleRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class RoleService {
    
    private final RoleRepository repository;

    // get all data
    public List<RoleEntities> getAllRole() {
        return repository.findAll();
    }

    // create or update
    public RoleEntities save(RoleEntities roleData) throws BadRequestException {
        if (!StringUtils.hasText(roleData.getName())) {
            throw new BadRequestException("Role Name required");
        }

        String loggedInUsername = SecurityContextHolder.getContext().getAuthentication().getName();

        roleData.setCreatedBy(loggedInUsername);
        roleData.setUpdatedBy(loggedInUsername);

        return repository.save(roleData);
    }

    // get by id
    public RoleEntities getRoleById(Long id) {
        return repository.findById(id).orElse(null);
    }

    // delete
    public void deleteRoleById(Long id) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("Role not found with ID: " + id);
        }
        repository.deleteById(id);
    }
}
