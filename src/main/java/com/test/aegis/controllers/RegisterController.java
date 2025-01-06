package com.test.aegis.controllers;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.test.aegis.dto.ChangePasswordData;
import com.test.aegis.dto.ResponseData;
import com.test.aegis.dto.UserData;
import com.test.aegis.exceptions.ValidationGroups;
import com.test.aegis.models.entities.UserEntities;
import com.test.aegis.services.AuthService;
import com.test.aegis.services.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/auth")
public class RegisterController {
    
    private final UserService userService;
    private final AuthService authService;

    public RegisterController(UserService userService, AuthService authService) {
        this.userService = userService;
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<ResponseData<UserEntities>> save(@Valid @RequestBody UserData userData, Errors errors) throws BadRequestException {
        ResponseData<UserEntities> data = new ResponseData<>();

        if (errors.hasErrors()) {
            List<String> errorMessages = errors.getAllErrors()
                    .stream()
                    .map(error -> error.getDefaultMessage())
                    .collect(Collectors.toList());

            data.setSuccess(false);
            data.setMessage("Validation failed with " + errorMessages.size() + " errors.");
            data.setMessages(errorMessages);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(data);
        }

        try {
            UserEntities user = userService.createUser(userData);
            data.setSuccess(true);
            data.setMessage("User saved successfully.");
            data.setData(user);
            return ResponseEntity.status(HttpStatus.OK).body(data);
        } catch (IllegalArgumentException | BadRequestException ex) {
            data.setSuccess(false);
            data.setMessage(ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(data);
        }
    }

    @PutMapping("/change/password/{id}")
    public ResponseEntity<ResponseData<UserEntities>> change(@Validated @RequestBody ChangePasswordData changePasswordData, Errors errors, @PathVariable Long id) throws BadRequestException {
        ResponseData<UserEntities> response = new ResponseData<>();

        if (errors.hasErrors()) {
            List<String> errorMessages = errors.getAllErrors()
                    .stream()
                    .map(error -> error.getDefaultMessage())
                    .collect(Collectors.toList());

            response.setSuccess(false);
            response.setMessage("Validation failed.");
            response.setMessages(errorMessages);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        try {
            UserEntities updatedUser = authService.changePassword(changePasswordData, id);
            response.setSuccess(true);
            response.setMessage("Password changed successfully.");
            response.setData(updatedUser);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (BadRequestException ex) {
            response.setSuccess(false);
            response.setMessage(ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}
