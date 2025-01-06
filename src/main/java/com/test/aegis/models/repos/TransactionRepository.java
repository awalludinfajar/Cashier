package com.test.aegis.models.repos;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.test.aegis.models.entities.TransactionEntities;

@Repository
public interface TransactionRepository extends JpaRepository<TransactionEntities, Long> {
    List<TransactionEntities> findByTransactionCode(String code);
}
