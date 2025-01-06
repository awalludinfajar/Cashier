package com.test.aegis.services;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.coyote.BadRequestException;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.test.aegis.dto.UserData;
import com.test.aegis.models.entities.RoleEntities;
import com.test.aegis.models.entities.UserEntities;
import com.test.aegis.models.repos.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    
    private final UserRepository repository;
    private final ModelMapper modelMapper;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;

    // get all data
    public List<UserEntities> getAllUser() {
        return repository.findAll();
    }

    // create
    public UserEntities createUser(UserData userData) throws BadRequestException {

        if (!userData.getPassword().equals(userData.getConfirmPassword())) {
            throw new BadRequestException("Password and confirm password do not match.");
        }

        if (repository.existsByUsername(userData.getUsername())) {
            throw new IllegalArgumentException("Username already exists.");
        }

        UserEntities user = modelMapper.map(userData, UserEntities.class);

        RoleEntities roles = roleService.getRoleById(userData.getRoles());
        if (roles == null) {
            throw new IllegalArgumentException("Role is not exists.");
        }

        user.setPassword(passwordEncoder.encode(userData.getPassword()));
        user.setRoleEntities(Set.of(roles));

        String loggedInUsername = SecurityContextHolder.getContext().getAuthentication().getName();

        user.setCreatedBy(loggedInUsername);
        user.setUpdatedBy(loggedInUsername);

        return repository.save(user);
    }

    // update
    public UserEntities updateUser(UserData userData, Long id) throws BadRequestException {
        UserEntities existingUser = repository.findById(id)
                .orElseThrow(() -> new BadRequestException("User with ID " + id + " not found."));

        existingUser.setFullName(userData.getFullName());
        existingUser.setEmail(userData.getEmail());
        existingUser.setUsername(userData.getUsername());
        existingUser.setDescription(userData.getDescription());

        RoleEntities roles = roleService.getRoleById(userData.getRoles());

        Set<RoleEntities> modifiableRoles = new HashSet<>();
        modifiableRoles.add(roles);

        existingUser.setRoleEntities(modifiableRoles);

        String loggedInUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        existingUser.setUpdatedBy(loggedInUsername);

        return repository.save(existingUser);
    }

    // get a user by Id
    public UserEntities getUserById(Long id) {
        return repository.findById(id).orElse(null);
    }

    // get a user by username
    public UserEntities getUserByUsername(String username) {
        return repository.findByUsername(username).orElse(null);
    }

    // delete user
    public void deleteUserById(Long id) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("User not found with ID: " + id);
        }
        repository.deleteById(id);
    }
}
