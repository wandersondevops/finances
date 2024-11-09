package com.account;

import com.account.dto.ClientDTO;
import com.account.entity.Account;
import com.account.entity.Transaction;
import com.account.repository.AccountRepository;
import com.account.repository.TransactionRepository;
import com.account.service.ReportService;
import com.account.service.ClientServiceCommunicator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ReportServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private ClientServiceCommunicator clientServiceCommunicator;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private ReportService reportService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void fetchReport_Success() {
        // Arrange
        UUID clientId = UUID.randomUUID();
        String startDate = "2024-11-01T00:00:00";
        String endDate = "2024-11-09T23:59:59";
        String url = "http://localhost:8081/reportes?fecha={startDate},{endDate}&client={clientId}";

        Map<String, Object> expectedResponse = new HashMap<>();
        expectedResponse.put("key", "value");

        when(restTemplate.exchange(eq(url), eq(HttpMethod.GET), isNull(), any(ParameterizedTypeReference.class), eq(startDate), eq(endDate), eq(clientId)))
                .thenReturn(ResponseEntity.ok(expectedResponse));

        // Act
        Map<String, Object> result = reportService.fetchReport(startDate, endDate, clientId);

        // Assert
        assertEquals(expectedResponse, result);
    }

    @Test
    void fetchReport_Failure() {
        // Arrange
        UUID clientId = UUID.randomUUID();
        String startDate = "2024-11-01T00:00:00";
        String endDate = "2024-11-09T23:59:59";
        String url = "http://localhost:8081/reportes?fecha={startDate},{endDate}&client={clientId}";

        when(restTemplate.exchange(eq(url), eq(HttpMethod.GET), isNull(), any(ParameterizedTypeReference.class), eq(startDate), eq(endDate), eq(clientId)))
                .thenThrow(new RuntimeException("Error fetching report"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> reportService.fetchReport(startDate, endDate, clientId));
        assertEquals("Error fetching report", exception.getMessage());
    }

    @Test
    void generateAccountStatement_ClientNotFound() {
        // Arrange
        UUID clientId = UUID.randomUUID();
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = LocalDateTime.now().plusDays(1);

        when(clientServiceCommunicator.getClientDetails(clientId)).thenReturn(null);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> reportService.generateAccountStatement(clientId, startDate, endDate));
        assertEquals("Client not found", exception.getMessage());
    }

    @Test
    void generateAccountStatement_NoAccountsFound() {
        // Arrange
        UUID clientId = UUID.randomUUID();
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = LocalDateTime.now().plusDays(1);
        ClientDTO clientDTO = new ClientDTO();
        clientDTO.setClientId(clientId);

        when(clientServiceCommunicator.getClientDetails(clientId)).thenReturn(clientDTO);
        when(accountRepository.findByClientId(clientId)).thenReturn(Collections.emptyList());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> reportService.generateAccountStatement(clientId, startDate, endDate));
        assertEquals("No accounts found for the client", exception.getMessage());
    }

    @Test
    void generateAccountStatement_Success() {
        // Arrange
        UUID clientId = UUID.randomUUID();
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = LocalDateTime.now().plusDays(1);
        ClientDTO clientDTO = new ClientDTO();
        clientDTO.setClientId(clientId);

        Account account = new Account();
        account.setAccountId(UUID.randomUUID());

        Transaction transaction = new Transaction();
        transaction.setAmount(100.0);

        when(clientServiceCommunicator.getClientDetails(clientId)).thenReturn(clientDTO);
        when(accountRepository.findByClientId(clientId)).thenReturn(Collections.singletonList(account));
        when(transactionRepository.findByAccountIdAndTransactionDateBetween(account.getAccountId(), startDate, endDate))
                .thenReturn(Collections.singletonList(transaction));

        // Act
        Map<String, Object> result = reportService.generateAccountStatement(clientId, startDate, endDate);

        // Assert
        assertNotNull(result);
        assertEquals(clientDTO, result.get("client"));

        List<Map<String, Object>> accountsData = (List<Map<String, Object>>) result.get("accounts");
        assertNotNull(accountsData);
        assertEquals(1, accountsData.size());

        Map<String, Object> accountData = accountsData.get(0);
        assertEquals(account, accountData.get("accountDetails"));

        List<Transaction> transactions = (List<Transaction>) accountData.get("transactions");
        assertEquals(1, transactions.size());
        assertEquals(transaction, transactions.get(0));
    }

    @Test
    void generateAccountStatement_NoTransactions() {
        // Arrange
        UUID clientId = UUID.randomUUID();
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = LocalDateTime.now().plusDays(1);
        ClientDTO clientDTO = new ClientDTO();
        clientDTO.setClientId(clientId);

        Account account = new Account();
        account.setAccountId(UUID.randomUUID());

        when(clientServiceCommunicator.getClientDetails(clientId)).thenReturn(clientDTO);
        when(accountRepository.findByClientId(clientId)).thenReturn(Collections.singletonList(account));
        when(transactionRepository.findByAccountIdAndTransactionDateBetween(account.getAccountId(), startDate, endDate))
                .thenReturn(Collections.emptyList());

        // Act
        Map<String, Object> result = reportService.generateAccountStatement(clientId, startDate, endDate);

        // Assert
        assertNotNull(result);
        assertEquals(clientDTO, result.get("client"));

        List<Map<String, Object>> accountsData = (List<Map<String, Object>>) result.get("accounts");
        assertNotNull(accountsData);
        assertEquals(1, accountsData.size());

        Map<String, Object> accountData = accountsData.get(0);
        assertEquals(account, accountData.get("accountDetails"));

        List<Transaction> transactions = (List<Transaction>) accountData.get("transactions");
        assertTrue(transactions.isEmpty());
    }
}

