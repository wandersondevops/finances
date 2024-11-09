package com.account;

import com.account.entity.Account;
import com.account.repository.AccountRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AccountControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Account account;

    @BeforeEach
    void setUp() {
        // Prepare a sample account for tests
        account = new Account();
        account.setAccountNumber(Long.valueOf("987654321"));
        account.setAccountType("Checking");
        account.setBalance(1000.0);
        account.setClientId(UUID.randomUUID());
        accountRepository.save(account);
    }

    @Test
    void getAllAccounts_ShouldReturnAllAccounts() throws Exception {
        mockMvc.perform(get("/cuentas"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void getAccountById_ShouldReturnAccount_WhenAccountExists() throws Exception {
        mockMvc.perform(get("/cuentas/{accountId}", account.getAccountId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountNumber").value(account.getAccountNumber()))
                .andExpect(jsonPath("$.accountType").value(account.getAccountType()));
    }

    @Test
    void getAccountById_ShouldReturnNotFound_WhenAccountDoesNotExist() throws Exception {
        UUID nonexistentId = UUID.randomUUID();
        mockMvc.perform(get("/cuentas/{accountId}", nonexistentId))
                .andExpect(status().isNotFound());
    }

    @Test
    void createAccounts_ShouldCreateAndReturnAccounts() throws Exception {
        Account newAccount = new Account();
        newAccount.setAccountNumber(Long.valueOf("987654321"));
        newAccount.setAccountType("Savings");
        newAccount.setBalance(500.0);
        newAccount.setClientId(UUID.randomUUID());
        List<Account> accounts = List.of(newAccount);

        mockMvc.perform(post("/cuentas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(accounts)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].accountNumber").value("987654321"))
                .andExpect(jsonPath("$[0].accountType").value("Savings"));
    }

    @Test
    void updateAccount_ShouldUpdateAndReturnAccount_WhenAccountExists() throws Exception {
        Account updatedAccount = new Account();
        updatedAccount.setAccountType("Business");
        updatedAccount.setBalance(2000.0);

        mockMvc.perform(put("/cuentas/{accountId}", account.getAccountId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedAccount)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountType").value("Business"))
                .andExpect(jsonPath("$.balance").value(2000.0));
    }

    @Test
    void updateAccount_ShouldReturnNotFound_WhenAccountDoesNotExist() throws Exception {
        Account updatedAccount = new Account();
        updatedAccount.setAccountType("Business");
        updatedAccount.setBalance(2000.0);

        UUID nonexistentId = UUID.randomUUID();
        mockMvc.perform(put("/cuentas/{accountId}", nonexistentId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedAccount)))
                .andExpect(status().isNotFound());
    }

    @Test
    void partialUpdateAccount_ShouldPartiallyUpdateAndReturnAccount_WhenAccountExists() throws Exception {
        Account partialUpdate = new Account();
        partialUpdate.setBalance(1500.0);

        mockMvc.perform(patch("/cuentas/{accountId}", account.getAccountId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(partialUpdate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(1500.0));
    }

    @Test
    void partialUpdateAccount_ShouldReturnNotFound_WhenAccountDoesNotExist() throws Exception {
        Account partialUpdate = new Account();
        partialUpdate.setBalance(1500.0);

        UUID nonexistentId = UUID.randomUUID();
        mockMvc.perform(patch("/cuentas/{accountId}", nonexistentId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(partialUpdate)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteAllAccounts_ShouldDeleteAllAccounts() throws Exception {
        mockMvc.perform(delete("/cuentas"))
                .andExpect(status().isNoContent());
        assertThat(accountRepository.findAll()).isEmpty();
    }

    @Test
    void deleteAccount_ShouldDeleteAccount_WhenAccountExists() throws Exception {
        mockMvc.perform(delete("/cuentas/{accountId}", account.getAccountId()))
                .andExpect(status().isNoContent());
        assertThat(accountRepository.existsById(account.getAccountId())).isFalse();
    }

    @Test
    void deleteAccount_ShouldReturnNotFound_WhenAccountDoesNotExist() throws Exception {
        UUID nonexistentId = UUID.randomUUID();
        mockMvc.perform(delete("/cuentas/{accountId}", nonexistentId))
                .andExpect(status().isNotFound());
    }
}

