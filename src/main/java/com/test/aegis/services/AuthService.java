package com.test.aegis.services;

import org.apache.coyote.BadRequestException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.test.aegis.dto.AuthData;
import com.test.aegis.dto.ChangePasswordData;
import com.test.aegis.models.entities.UserEntities;
import com.test.aegis.models.repos.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService implements UserDetailsService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    
    public UserEntities changePassword(ChangePasswordData changePasswordData, Long id) throws BadRequestException {
        UserEntities user = repository.findById(id)
            .orElseThrow(() -> new BadRequestException("User with ID " + id + " not found."));

        if (changePasswordData.getCurrentPassword() == null) {
            throw new BadRequestException("Current password is required and cannot be null.");
        }

        if (!passwordEncoder.matches(changePasswordData.getCurrentPassword(), user.getPassword())) {
            throw new BadRequestException("Current password is incorrect.");
        }

        if (!changePasswordData.getNewPassword().equals(changePasswordData.getConfirmPassword())) {
            throw new BadRequestException("New password and confirm password do not match.");
        }

        user.setPassword(passwordEncoder.encode(changePasswordData.getNewPassword()));

        return repository.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntities userAuthEntities = repository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("Username "+ username +" not found"));
            
        return AuthData.build(userAuthEntities);
    }
}
