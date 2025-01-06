package com.test.aegis.services;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.coyote.BadRequestException;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.test.aegis.dto.TransactionData;
import com.test.aegis.dto.TransactionReport;
import com.test.aegis.models.entities.ProductEntities;
import com.test.aegis.models.entities.TransactionEntities;
import com.test.aegis.models.entities.UserEntities;
import com.test.aegis.models.repos.TransactionRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class TransactionService {
    
    private final TransactionRepository transactionRepository;
    private final ProductService productService;
    private final UserService userService;
    private final ModelMapper modelMapper;

    // get all list transaction
    public List<TransactionEntities> getAllTrasaction() throws BadRequestException {
        return transactionRepository.findAll();
    }

    // create
    public TransactionEntities createTransaction(TransactionData transactionData, String code) throws BadRequestException {

        ProductEntities productEntities = productService.getProductById(transactionData.getProductId());
        if (productEntities == null) {
            throw new IllegalArgumentException("Product is not exists.");
        }

        Long totalPrice = productEntities.getPrice().intValue()*transactionData.getQuantity();

        TransactionEntities transaction = new TransactionEntities();
        transaction.setTransactionCode(code);
        transaction.setProductEntities(Set.of(productEntities));
        transaction.setQuantity(transactionData.getQuantity());
        transaction.setTotalPrice(new BigDecimal(totalPrice));

        UserEntities userEntities = userService.getUserById(transactionData.getUserId());
        if (userEntities == null) {
            throw new IllegalArgumentException("User is not exists.");
        }

        transaction.setUserEntities(Set.of(userEntities));

        String loggedInUsername = SecurityContextHolder.getContext().getAuthentication().getName();

        transaction.setCreatedBy(loggedInUsername);
        transaction.setUpdatedBy(loggedInUsername);

        return transactionRepository.save(transaction);
    }

    // update
    public List<TransactionEntities> getTransactionByCode(String code) throws BadRequestException {
        List<TransactionEntities> transactionEntities = transactionRepository.findByTransactionCode(code);
        if (transactionEntities == null) {
            throw new IllegalArgumentException("Transaction with code " + code + " not found.");
        }
        return transactionEntities;
    }

    // get by id
    public TransactionEntities getTransactionById(Long id) throws BadRequestException {
        return transactionRepository.findById(id).orElseThrow(() -> new BadRequestException("Transaction with ID " + id + " not found."));
    }

    // delete
    public void deleteTransactionId(Long id) {
        if (!transactionRepository.existsById(id)) {
            throw new IllegalArgumentException("Transaction not found with ID: " + id);
        }
        transactionRepository.deleteById(id);
    }

    // report
    public Map<String, TransactionReport> generateCumulativeSummary(List<TransactionEntities> transactions) {
        return transactions.stream()
            .collect(Collectors.groupingBy(
                TransactionEntities::getTransactionCode,
                Collectors.reducing(
                    new TransactionReport(BigDecimal.ZERO, (long) 0, null, Instant.MAX),
                    transaction -> new TransactionReport(
                        transaction.getTotalPrice(),
                        transaction.getQuantity(),
                        transaction.getCreatedBy(),
                        transaction.getCreatedAt()
                    ),
                    TransactionReport::combine
                )
            ));
    }
}
