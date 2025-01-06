package com.test.aegis.controllers;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.test.aegis.dto.ResponseData;
import com.test.aegis.dto.UserData;
import com.test.aegis.exceptions.ValidationGroups;
import com.test.aegis.models.entities.UserEntities;
import com.test.aegis.services.UserService;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {
    
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    public ResponseData<List<UserEntities>> getAll() {
        ResponseData<List<UserEntities>> response = new ResponseData<>();
        try {
            List<UserEntities> user = userService.getAllUser();
            response.setSuccess(true);
            response.setData(user);
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage("An error occurred while fetching roles: " + e.getMessage());
        }

        return response;
    }

    @GetMapping("/{id}")
    public ResponseData<UserEntities> getById(@PathVariable Long id) {
        ResponseData<UserEntities> response = new ResponseData<>();
        try {
            UserEntities user = userService.getUserById(id);
            response.setSuccess(true);
            response.setData(user);
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage("An error occurred while fetching roles: " + e.getMessage());
        }

        return response;
    }

    @PostMapping("/create")
    public ResponseEntity<ResponseData<UserEntities>> save(@Validated(ValidationGroups.Create.class) @RequestBody UserData userData, Errors errors) throws BadRequestException {
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

    @PutMapping("/update/{id}")
    public ResponseEntity<ResponseData<UserEntities>> update(@Validated(ValidationGroups.Update.class) @RequestBody UserData userData, Errors errors, @PathVariable Long id) throws BadRequestException {
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
            UserEntities user = userService.updateUser(userData, id);
            data.setSuccess(true);
            data.setMessage("User update successfully.");
            data.setData(user);
            return ResponseEntity.status(HttpStatus.OK).body(data);
        } catch (IllegalArgumentException | BadRequestException ex) {
            data.setSuccess(false);
            data.setMessage(ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(data);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ResponseData<String>> delete(@PathVariable Long id) {
        ResponseData<String> data = new ResponseData<>();

        try {
            userService.deleteUserById(id);

            data.setSuccess(true);
            data.setMessage("User with ID " + id + " deleted successfully");
            return ResponseEntity.status(HttpStatus.OK).body(data);
        } catch (IllegalArgumentException ex) {
            data.setSuccess(false);
            data.setMessage("User with ID " + id + " not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(data);
        }
    }
}
