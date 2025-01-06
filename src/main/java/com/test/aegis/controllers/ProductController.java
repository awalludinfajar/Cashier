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

import com.test.aegis.dto.ProductData;
import com.test.aegis.dto.ResponseData;
import com.test.aegis.exceptions.ValidationGroups;
import com.test.aegis.models.entities.ProductEntities;
import com.test.aegis.services.ProductService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/product")
public class ProductController {
    
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/")
    public ResponseData<List<ProductEntities>> getAll() {
        ResponseData<List<ProductEntities>> response = new ResponseData<>();

        try {
            List<ProductEntities> product = productService.getAllProduct();
            response.setSuccess(true);
            response.setData(product);
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage("An error occurred while fetching roles: " + e.getMessage());
        }

        return response;
    }

    @GetMapping("/{id}")
    public ResponseData<ProductEntities> getById(@PathVariable Long id) {
        ResponseData<ProductEntities> response = new ResponseData<>();

        try {
            ProductEntities product = productService.getProductById(id);
            response.setSuccess(true);
            response.setData(product);
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage("An error occurred while fetching roles: " + e.getMessage());
        }

        return response;
    }

    @PostMapping("/create")
    public ResponseEntity<ResponseData<ProductEntities>> create(@Valid @RequestBody ProductData productData, Errors errors) throws BadRequestException {
        ResponseData<ProductEntities> data = new ResponseData<>();

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
            ProductEntities product = productService.createProduct(productData);
            data.setSuccess(true);
            data.setMessage("Product saved successfully.");
            data.setData(product);
            return ResponseEntity.status(HttpStatus.OK).body(data);
        } catch (IllegalArgumentException | BadRequestException ex) {
            data.setSuccess(false);
            data.setMessage(ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(data);
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ResponseData<ProductEntities>> update(@Valid @RequestBody ProductData productData, Errors errors, @PathVariable Long id) throws BadRequestException {
        ResponseData<ProductEntities> data = new ResponseData<>();

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
            ProductEntities product = productService.updateProduct(productData, id);
            data.setSuccess(true);
            data.setMessage("Product update successfully.");
            data.setData(product);
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
            productService.deleteProductById(id);;

            data.setSuccess(true);
            data.setMessage("Product with ID " + id + " deleted successfully");
            return ResponseEntity.status(HttpStatus.OK).body(data);
        } catch (IllegalArgumentException ex) {
            data.setSuccess(false);
            data.setMessage("Product with ID " + id + " not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(data);
        }
    }
}
