package com.test.aegis.services;

import java.util.List;

import org.apache.coyote.BadRequestException;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.test.aegis.dto.ProductData;
import com.test.aegis.models.entities.ProductEntities;
import com.test.aegis.models.repos.ProductRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductService {
    
    private final ProductRepository repository;
    private final ModelMapper modelMapper;

    // get all product
    public List<ProductEntities> getAllProduct() {
        return repository.findAll();
    }

    // create
    public ProductEntities createProduct(ProductData productData) throws BadRequestException {
        ProductEntities product = modelMapper.map(productData, ProductEntities.class);

        String loggedInUsername = SecurityContextHolder.getContext().getAuthentication().getName();

        product.setCreatedBy(loggedInUsername);
        product.setUpdatedBy(loggedInUsername);

        return repository.save(product);
    }

    // update
    public ProductEntities updateProduct(ProductData productData, Long id) throws BadRequestException {
        ProductEntities productEntities = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with ID: " + id));

        productEntities.setName(productData.getName());
        productEntities.setCategory(productData.getCategory());
        productEntities.setPrice(productData.getPrice());
        productEntities.setQuantity(productData.getQuantity());
        productEntities.setDescription(productData.getDescription());

        String loggedInUsername = SecurityContextHolder.getContext().getAuthentication().getName();

        productEntities.setCreatedBy(loggedInUsername);
        productEntities.setUpdatedBy(loggedInUsername);

        return repository.save(productEntities);
    }

    // get data by id
    public ProductEntities getProductById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with ID: " + id));
    }

    // delete data by id
    public void deleteProductById(Long id) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("Product not found with ID: " + id);
        }
        repository.deleteById(id);
    }
}
