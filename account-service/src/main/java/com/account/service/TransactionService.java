package com.account.service;

import com.account.entity.Transaction;
import com.account.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    public List<Transaction> getTransactionsByAccountAndDateRange(UUID accountId, LocalDateTime startDate, LocalDateTime endDate) {
        return transactionRepository.findByAccountIdAndTransactionDateBetween(accountId, startDate, endDate);
    }

    public Transaction createTransaction(Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    public List<Transaction> createTransactions(List<Transaction> transactions) {
        return transactionRepository.saveAll(transactions);
    }

    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    public Optional<Transaction> getTransactionById(UUID transactionId) {
        return transactionRepository.findById(transactionId);
    }

    public Transaction updateTransaction(UUID transactionId, Transaction updatedTransaction) {
        return transactionRepository.findById(transactionId)
            .map(transaction -> {
                transaction.setAmount(updatedTransaction.getAmount());
                transaction.setCredit(updatedTransaction.isCredit());
                transaction.setTransactionDate(updatedTransaction.getTransactionDate());
                return transactionRepository.save(transaction);
            })
            .orElseThrow(() -> new RuntimeException("Transaction not found"));
    }

    public void deleteTransaction(UUID transactionId) {
        transactionRepository.deleteById(transactionId);
    }

    public void deleteAllTransactions() {
        transactionRepository.deleteAll();
    }
}
