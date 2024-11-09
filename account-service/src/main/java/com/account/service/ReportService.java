package com.account.service;

import com.account.dto.ClientDTO;
import com.account.entity.Account;
import com.account.entity.Transaction;
import com.account.repository.AccountRepository;
import com.account.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class ReportService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private ClientServiceCommunicator clientServiceCommunicator;

    @Autowired  // Inject RestTemplate here
    private RestTemplate restTemplate;

    public Map<String, Object> fetchReport(String startDate, String endDate, UUID clientId) {
        String url = "http://localhost:8081/reportes?fecha={startDate},{endDate}&client={clientId}";

        ParameterizedTypeReference<Map<String, Object>> responseType = new ParameterizedTypeReference<>() {};
        
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                responseType,
                startDate,
                endDate,
                clientId
        );
        
        return response.getBody();
    }

    public Map<String, Object> generateAccountStatement(UUID clientId, LocalDateTime startDate, LocalDateTime endDate) {
        Map<String, Object> reportData = new HashMap<>();

        try {

            ClientDTO client = clientServiceCommunicator.getClientDetails(clientId); 
            if (client == null) {
                throw new IllegalArgumentException("Client not found");
            }
            reportData.put("client", client);


            List<Account> accounts = accountRepository.findByClientId(clientId);
            if (accounts.isEmpty()) {
                throw new IllegalArgumentException("No accounts found for the client");
            }

            List<Map<String, Object>> accountsData = new ArrayList<>();
            for (Account account : accounts) {
                Map<String, Object> accountData = new HashMap<>();
                accountData.put("accountDetails", account);

                List<Transaction> transactions = transactionRepository.findByAccountIdAndTransactionDateBetween(
                        account.getAccountId(), startDate, endDate);
                accountData.put("transactions", transactions);
                accountsData.add(accountData);
            }
            reportData.put("accounts", accountsData);
        } catch (Exception e) {

            e.printStackTrace();
            throw new RuntimeException("Error generating account statement", e);
        }
        return reportData;
    }
}
