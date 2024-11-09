package com.account;

import com.account.entity.Transaction;
import com.account.repository.TransactionRepository;
import com.account.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TransactionService transactionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getTransactionsByAccountAndDateRange_ShouldReturnTransactions_WhenTransactionsExist() {
        UUID accountId = UUID.randomUUID();
        LocalDateTime startDate = LocalDateTime.now().minusDays(5);
        LocalDateTime endDate = LocalDateTime.now();
        List<Transaction> transactions = Arrays.asList(new Transaction(), new Transaction());
        when(transactionRepository.findByAccountIdAndTransactionDateBetween(accountId, startDate, endDate)).thenReturn(transactions);

        List<Transaction> result = transactionService.getTransactionsByAccountAndDateRange(accountId, startDate, endDate);

        assertEquals(transactions, result);
        verify(transactionRepository, times(1)).findByAccountIdAndTransactionDateBetween(accountId, startDate, endDate);
    }

    @Test
    void createTransaction_ShouldSaveAndReturnTransaction() {
        Transaction transaction = new Transaction();
        when(transactionRepository.save(transaction)).thenReturn(transaction);

        Transaction result = transactionService.createTransaction(transaction);

        assertEquals(transaction, result);
        verify(transactionRepository, times(1)).save(transaction);
    }

    @Test
    void createTransactions_ShouldSaveAndReturnTransactions() {
        List<Transaction> transactions = Arrays.asList(new Transaction(), new Transaction());
        when(transactionRepository.saveAll(transactions)).thenReturn(transactions);

        List<Transaction> result = transactionService.createTransactions(transactions);

        assertEquals(transactions, result);
        verify(transactionRepository, times(1)).saveAll(transactions);
    }

    @Test
    void getAllTransactions_ShouldReturnAllTransactions() {
        List<Transaction> transactions = Arrays.asList(new Transaction(), new Transaction());
        when(transactionRepository.findAll()).thenReturn(transactions);

        List<Transaction> result = transactionService.getAllTransactions();

        assertEquals(transactions, result);
        verify(transactionRepository, times(1)).findAll();
    }

    @Test
    void getTransactionById_ShouldReturnTransaction_WhenTransactionExists() {
        UUID transactionId = UUID.randomUUID();
        Transaction transaction = new Transaction();
        when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(transaction));

        Optional<Transaction> result = transactionService.getTransactionById(transactionId);

        assertTrue(result.isPresent());
        assertEquals(transaction, result.get());
        verify(transactionRepository, times(1)).findById(transactionId);
    }

    @Test
    void getTransactionById_ShouldReturnEmpty_WhenTransactionDoesNotExist() {
        UUID transactionId = UUID.randomUUID();
        when(transactionRepository.findById(transactionId)).thenReturn(Optional.empty());

        Optional<Transaction> result = transactionService.getTransactionById(transactionId);

        assertFalse(result.isPresent());
        verify(transactionRepository, times(1)).findById(transactionId);
    }

    @Test
    void updateTransaction_ShouldUpdateAndReturnTransaction_WhenTransactionExists() {
        UUID transactionId = UUID.randomUUID();
        Transaction existingTransaction = new Transaction();
        Transaction updatedTransaction = new Transaction();
        updatedTransaction.setAmount(500.0);
        updatedTransaction.setCredit(true);
        updatedTransaction.setTransactionDate(LocalDateTime.now());

        when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(existingTransaction));
        when(transactionRepository.save(existingTransaction)).thenReturn(existingTransaction);

        Transaction result = transactionService.updateTransaction(transactionId, updatedTransaction);

        assertEquals(500.0, existingTransaction.getAmount());
        assertTrue(existingTransaction.isCredit());
        assertEquals(updatedTransaction.getTransactionDate(), existingTransaction.getTransactionDate());
        verify(transactionRepository, times(1)).findById(transactionId);
        verify(transactionRepository, times(1)).save(existingTransaction);
    }

    @Test
    void updateTransaction_ShouldThrowException_WhenTransactionDoesNotExist() {
        UUID transactionId = UUID.randomUUID();
        Transaction updatedTransaction = new Transaction();
        when(transactionRepository.findById(transactionId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> transactionService.updateTransaction(transactionId, updatedTransaction));
        verify(transactionRepository, times(1)).findById(transactionId);
    }

    @Test
    void deleteTransaction_ShouldDeleteTransaction_WhenTransactionExists() {
        UUID transactionId = UUID.randomUUID();

        transactionService.deleteTransaction(transactionId);

        verify(transactionRepository, times(1)).deleteById(transactionId);
    }

    @Test
    void deleteAllTransactions_ShouldDeleteAllTransactions() {
        transactionService.deleteAllTransactions();

        verify(transactionRepository, times(1)).deleteAll();
    }
}

