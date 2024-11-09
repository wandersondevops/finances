package com.account;

import com.account.controller.TransactionController;
import com.account.entity.Transaction;
import com.account.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;

@WebMvcTest(TransactionController.class)
class TransactionControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransactionService transactionService;

    private UUID transactionId;
    private Transaction transaction;

    @BeforeEach
    void setUp() {
        transactionId = UUID.randomUUID();
        transaction = new Transaction();
        transaction.setTransactionId(transactionId);
        transaction.setAmount(100.0);
        transaction.setCredit(true);
    }

    @Test
    void createTransactions_ShouldReturnCreatedTransactions() throws Exception {
        List<Transaction> transactions = Arrays.asList(transaction);
        when(transactionService.createTransactions(any())).thenReturn(transactions);

        mockMvc.perform(post("/movimientos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[{\"amount\":100.0,\"credit\":true}]"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].amount").value(100.0))
                .andExpect(jsonPath("$[0].credit").value(true));
    }

    @Test
    void getAllTransactions_ShouldReturnAllTransactions() throws Exception {
        List<Transaction> transactions = Arrays.asList(transaction);
        when(transactionService.getAllTransactions()).thenReturn(transactions);

        mockMvc.perform(get("/movimientos")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].transactionId").value(transactionId.toString()))
                .andExpect(jsonPath("$[0].amount").value(100.0))
                .andExpect(jsonPath("$[0].credit").value(true));
    }

    @Test
    void getTransactionById_ShouldReturnTransaction_WhenTransactionExists() throws Exception {
        when(transactionService.getTransactionById(transactionId)).thenReturn(Optional.of(transaction));

        mockMvc.perform(get("/movimientos/{transactionId}", transactionId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transactionId").value(transactionId.toString()))
                .andExpect(jsonPath("$.amount").value(100.0))
                .andExpect(jsonPath("$.credit").value(true));
    }

    @Test
    void getTransactionById_ShouldReturnNotFound_WhenTransactionDoesNotExist() throws Exception {
        when(transactionService.getTransactionById(transactionId)).thenReturn(Optional.empty());

        mockMvc.perform(get("/movimientos/{transactionId}", transactionId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateTransaction_ShouldReturnUpdatedTransaction_WhenTransactionExists() throws Exception {
        transaction.setAmount(150.0);
        when(transactionService.updateTransaction(eq(transactionId), any(Transaction.class))).thenReturn(transaction);

        mockMvc.perform(patch("/movimientos/{transactionId}", transactionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\":150.0}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(150.0));
    }

    @Test
    void updateTransaction_ShouldReturnNotFound_WhenTransactionDoesNotExist() throws Exception {
        when(transactionService.updateTransaction(eq(transactionId), any(Transaction.class)))
                .thenThrow(new RuntimeException("Transaction not found"));

        mockMvc.perform(patch("/movimientos/{transactionId}", transactionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\":150.0}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteTransaction_ShouldReturnNoContent() throws Exception {
        doNothing().when(transactionService).deleteTransaction(transactionId);

        mockMvc.perform(delete("/movimientos/{transactionId}", transactionId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteAllTransactions_ShouldReturnNoContent() throws Exception {
        doNothing().when(transactionService).deleteAllTransactions();

        mockMvc.perform(delete("/movimientos")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }
}

