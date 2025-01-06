package com.test.aegis.models.repos;

import org.springframework.data.jpa.repository.JpaRepository;

import com.test.aegis.dto.RoleData;
import com.test.aegis.models.entities.RoleEntities;

public interface RoleRepository extends JpaRepository<RoleEntities, Long>{

    RoleData save(RoleData roleData);
    
}
