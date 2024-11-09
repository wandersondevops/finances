package com.account.listener;

import com.account.entity.Account;
import com.account.service.AccountService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class AccountMessageListener {

    @Autowired
    private AccountService accountService;

    @RabbitListener(queues = "account.request.queue")
    public List<Account> handleGetAllAccountsRequest(String message) {
        if (message.equals("GetAllAccounts")) {
            return accountService.getAllAccounts();
        }
        return List.of();
    }

    public String handleGetAccountByIdRequest(String message) {
        if (message.startsWith("GetAccountById:")) {
            UUID accountId = UUID.fromString(message.split(":")[1]); // Parse UUID
            Account account = accountService.getAccountById(accountId).orElse(null);
    
            if (account != null) {
                ObjectMapper objectMapper = new ObjectMapper();
                try {
                    return objectMapper.writeValueAsString(account);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException("Failed to serialize account information", e);
                }
            }
        }
        return null;
    }

    @RabbitListener(queues = "account.creation.queue")
    public void handleAccountCreation(Account account) {
        accountService.createAccounts(List.of(account));
    }

    @RabbitListener(queues = "account.update.queue")
    public void handleAccountUpdate(Account account) {
        accountService.updateAccount(account.getAccountId(), account);
    }

    @RabbitListener(queues = "account.deletion.queue")
    public void handleAccountDeletion(String accountId) {
        UUID uuid = UUID.fromString(accountId);
        accountService.deleteAccount(uuid);
    }
    
    @RabbitListener(queues = "account.response.queue")
    public void handleGenerateAccountNumberResponse(String message) {
        if (message.startsWith("GeneratedAccountNumber:")) {
            String accountNumber = message.split(":")[1];
            System.out.println("Generated account number: " + accountNumber);
        }
    }
}
