package com.test.aegis.controllers;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.coyote.BadRequestException;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.test.aegis.dto.ResponseData;
import com.test.aegis.dto.RoleData;
import com.test.aegis.models.entities.RoleEntities;
import com.test.aegis.services.RoleService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/role")
public class RoleController {
    
    private final RoleService roleService;
    private final ModelMapper modelMapper;

    public RoleController(ModelMapper modelMapper, RoleService roleService) {
        this.modelMapper = modelMapper;
        this.roleService = roleService;
    }

    @GetMapping("/")
    public ResponseData<List<RoleEntities>> getAll() {
        ResponseData<List<RoleEntities>> response = new ResponseData<>();
        try {
            List<RoleEntities> roles = roleService.getAllRole();
            response.setSuccess(true);
            response.setData(roles);
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage("An error occurred while fetching roles: " + e.getMessage());
        }

        return response;
    }

    @GetMapping("/{id}")
    public ResponseData<RoleEntities> getById(@PathVariable Long id) {
        ResponseData<RoleEntities> response = new ResponseData<>();
        try {
            RoleEntities roles = roleService.getRoleById(id);
            response.setSuccess(true);
            response.setData(roles);
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage("An error occurred while fetching roles: " + e.getMessage());
        }

        return response;
    }

    @PostMapping("/create")
    public ResponseEntity<ResponseData<RoleEntities>> save(@Valid @RequestBody RoleData roleData, Errors errors) throws BadRequestException {
        ResponseData<RoleEntities> data = new ResponseData<>();

        // validation input
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

        RoleEntities role = modelMapper.map(roleData, RoleEntities.class);
        RoleEntities saved = roleService.save(role);

        data.setSuccess(true);
        data.setMessage("Role saved successfully");
        data.setData(saved);

        return ResponseEntity.status(HttpStatus.CREATED).body(data);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ResponseData<RoleEntities>> update(@Valid @RequestBody RoleData roleData, Errors errors, @PathVariable Long id) throws BadRequestException {
        ResponseData<RoleEntities> data = new ResponseData<>();

        // validation input
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

        // check data is exists
        RoleEntities existingRole = roleService.getRoleById(id);
        if (existingRole == null) {
            data.setSuccess(false);
            data.setMessage("Role with ID " + id + " not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(data);
        }

        RoleEntities role = modelMapper.map(roleData, RoleEntities.class);
        role.setId(id);
        RoleEntities updated = roleService.save(role);

        data.setSuccess(true);
        data.setData(updated);
        data.setMessage("Role updated successfully");
        return ResponseEntity.status(HttpStatus.OK).body(data);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ResponseData<String>> delete(@PathVariable Long id) {
        ResponseData<String> data = new ResponseData<>();

        try {
            roleService.deleteRoleById(id);
    
            data.setSuccess(true);
            data.setMessage("Role with ID " + id + " deleted successfully");
            return ResponseEntity.status(HttpStatus.OK).body(data);
        } catch (IllegalArgumentException ex) {
            data.setSuccess(false);
            data.setMessage("Role with ID " + id + " not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(data);
        }
    }
}
