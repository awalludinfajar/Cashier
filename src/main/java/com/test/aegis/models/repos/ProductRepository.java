package com.test.aegis.models.repos;

import org.springframework.data.jpa.repository.JpaRepository;

import com.test.aegis.dto.ProductData;
import com.test.aegis.models.entities.ProductEntities;

public interface ProductRepository extends JpaRepository<ProductEntities, Long>{

    ProductData save(ProductData productData);
    
}
