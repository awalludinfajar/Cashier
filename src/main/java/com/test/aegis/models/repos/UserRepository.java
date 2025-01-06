package com.test.aegis.models.repos;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.test.aegis.dto.UserData;
import com.test.aegis.models.entities.UserEntities;

@Repository
public interface UserRepository extends JpaRepository<UserEntities, Long> {
    
    Boolean existsByUsername(String username);

    Optional<UserEntities> findByUsername(@Param("username") String username);

    UserEntities save(UserData user);
}
