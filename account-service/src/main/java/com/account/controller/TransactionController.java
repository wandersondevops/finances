package com.account.controller;

import com.account.entity.Transaction;
import com.account.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/movimientos")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @PostMapping
    public ResponseEntity<List<Transaction>> createTransactions(@RequestBody List<Transaction> transactions) {
        List<Transaction> createdTransactions = transactionService.createTransactions(transactions);
        return ResponseEntity.ok(createdTransactions);
    }

    @GetMapping
    public ResponseEntity<List<Transaction>> getAllTransactions() {
        List<Transaction> transactions = transactionService.getAllTransactions();
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/{transactionId}")
    public ResponseEntity<Transaction> getTransactionById(@PathVariable UUID transactionId) {
        Optional<Transaction> transaction = transactionService.getTransactionById(transactionId);
        return transaction.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PatchMapping("/{transactionId}")
    public ResponseEntity<Transaction> updateTransaction(@PathVariable UUID transactionId, @RequestBody Transaction updatedTransaction) {
        try {
            Transaction transaction = transactionService.updateTransaction(transactionId, updatedTransaction);
            return ResponseEntity.ok(transaction);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{transactionId}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable UUID transactionId) {
        transactionService.deleteTransaction(transactionId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAllTransactions() {
        transactionService.deleteAllTransactions();
        return ResponseEntity.noContent().build();
    }
}
