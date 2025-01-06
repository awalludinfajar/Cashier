package com.test.aegis.controllers;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.coyote.BadRequestException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.test.aegis.dto.ResponseData;
import com.test.aegis.dto.TransactionData;
import com.test.aegis.dto.TransactionReport;
import com.test.aegis.models.entities.TransactionEntities;
import com.test.aegis.services.TransactionService;

import jakarta.validation.Valid;

@PreAuthorize("hasRole('ROLE_KASIR')")
@RestController
@RequestMapping("/api/v1/transaction")
public class TransactionController {

    private final TransactionService transactionService;
    
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping("/")
    public ResponseData<List<TransactionEntities>> getAllTransaction() {
        ResponseData<List<TransactionEntities>> response = new ResponseData<>();
        try {
            List<TransactionEntities> transaction = transactionService.getAllTrasaction();
            response.setSuccess(true);
            response.setData(transaction);
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage("An error occurred while fetching roles: " + e.getMessage());
        }

        return response;
    }

    @GetMapping("/{id}")
    public ResponseData<TransactionEntities> getTransactionById(@PathVariable Long id) {
        ResponseData<TransactionEntities> response = new ResponseData<>();
        try {
            TransactionEntities transaction = transactionService.getTransactionById(id);
            response.setSuccess(true);
            response.setData(transaction);
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage("An error occurred while fetching roles: " + e.getMessage());
        }

        return response;
    }

    @GetMapping("/code/{code}")
    public ResponseData<List<TransactionEntities>> getTransactionByCode(@PathVariable String code) {
        ResponseData<List<TransactionEntities>> response = new ResponseData<>();
        try {
            List<TransactionEntities> transaction = transactionService.getTransactionByCode(code);
            response.setSuccess(true);
            response.setData(transaction);
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage("An error occurred while fetching roles: " + e.getMessage());
        }

        return response;
    }

    @PostMapping("/checkout")
    public ResponseEntity<ResponseData<List<TransactionEntities>>> store(@Valid @RequestBody List<TransactionData> transactionData, Errors errors) throws BadRequestException {
        ResponseData<List<TransactionEntities>> data = new ResponseData<>();

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
            LocalDateTime now = LocalDateTime.now();
            String timestamp = now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
            String code = "TRX-" + timestamp + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

            List<TransactionEntities> transactions = transactionData.stream()
                    .map(transaction -> {
                        try {
                            return transactionService.createTransaction(transaction, code);
                        } catch (BadRequestException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .collect(Collectors.toList());
    
            data.setSuccess(true);
            data.setData(transactions);
            return ResponseEntity.ok(data);
    
        } catch (Exception ex) {
            data.setSuccess(false);
            data.setMessages(List.of(ex.getMessage()));
            return ResponseEntity.badRequest().body(data);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ResponseData<String>> delete(@PathVariable Long id) {
        ResponseData<String> data = new ResponseData<>();

        try {
            transactionService.deleteTransactionId(id);

            data.setSuccess(true);
            data.setMessage("Transaction with ID " + id + " deleted successfully");
            return ResponseEntity.status(HttpStatus.OK).body(data);
        } catch (IllegalArgumentException ex) {
            data.setSuccess(false);
            data.setMessage("Transaction with ID " + id + " not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(data);
        }
    }

    @GetMapping("/report")
    public ResponseEntity<ResponseData<Map<String, TransactionReport>>> report(
        @RequestParam(value = "startDate", required = false) 
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate startDate,
        @RequestParam(value = "endDate", required = false) 
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate endDate
    ) {
        ResponseData<Map<String, TransactionReport>> data = new ResponseData<>();

        try {
            data.setSuccess(true);
            List<TransactionEntities> allTransactions = transactionService.getAllTrasaction();
            List<TransactionEntities> filteredTransactions = allTransactions.stream()
                .filter(transaction -> {
                    LocalDate createdAt = transaction.getCreatedAt().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
                    boolean isAfterStart = (startDate == null || !createdAt.isBefore(startDate));
                    boolean isBeforeEnd = (endDate == null || !createdAt.isAfter(endDate));
                    return isAfterStart && isBeforeEnd;
                })
                .toList();
    
            Map<String, TransactionReport> report = transactionService.generateCumulativeSummary(filteredTransactions);
            data.setData(report);
            return ResponseEntity.status(HttpStatus.OK).body(data);
        } catch (Exception e) {
            data.setSuccess(false);
            data.setMessage("Transaction not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(data);
        }
    }
}
