package com.account;

import com.account.entity.Account;
import com.account.exception.ResourceNotFoundException;
import com.account.repository.AccountRepository;
import com.account.service.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountService accountService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAccountsByClientId_ShouldReturnAccounts_WhenAccountsExist() {
        UUID clientId = UUID.randomUUID();
        List<Account> accounts = Arrays.asList(new Account(), new Account());
        when(accountRepository.findByClientId(clientId)).thenReturn(accounts);

        List<Account> result = accountService.getAccountsByClientId(clientId);

        assertEquals(accounts, result);
        verify(accountRepository, times(1)).findByClientId(clientId);
    }

    @Test
    void getAllAccounts_ShouldReturnAllAccounts() {
        List<Account> accounts = Arrays.asList(new Account(), new Account());
        when(accountRepository.findAll()).thenReturn(accounts);

        List<Account> result = accountService.getAllAccounts();

        assertEquals(accounts, result);
        verify(accountRepository, times(1)).findAll();
    }

    @Test
    void getAccountById_ShouldReturnAccount_WhenAccountExists() {
        UUID accountId = UUID.randomUUID();
        Account account = new Account();
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        Optional<Account> result = accountService.getAccountById(accountId);

        assertTrue(result.isPresent());
        assertEquals(account, result.get());
        verify(accountRepository, times(1)).findById(accountId);
    }

    @Test
    void getAccountById_ShouldReturnEmpty_WhenAccountDoesNotExist() {
        UUID accountId = UUID.randomUUID();
        when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

        Optional<Account> result = accountService.getAccountById(accountId);

        assertFalse(result.isPresent());
        verify(accountRepository, times(1)).findById(accountId);
    }

    @Test
    void createAccounts_ShouldSaveAccounts() {
        List<Account> accounts = Arrays.asList(new Account(), new Account());
        when(accountRepository.saveAll(accounts)).thenReturn(accounts);

        List<Account> result = accountService.createAccounts(accounts);

        assertEquals(accounts, result);
        verify(accountRepository, times(1)).saveAll(accounts);
    }

    @Test
    void updateAccount_ShouldUpdateAndReturnAccount_WhenAccountExists() {
        UUID accountId = UUID.randomUUID();
        Account existingAccount = new Account();
        Account updatedDetails = new Account();
        updatedDetails.setAccountType("UpdatedType");
        updatedDetails.setBalance(1000.0);

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(existingAccount));
        when(accountRepository.save(existingAccount)).thenReturn(existingAccount);

        Account result = accountService.updateAccount(accountId, updatedDetails);

        assertEquals("UpdatedType", existingAccount.getAccountType());
        assertEquals(1000.0, existingAccount.getBalance());
        verify(accountRepository, times(1)).findById(accountId);
        verify(accountRepository, times(1)).save(existingAccount);
    }

    @Test
    void updateAccount_ShouldThrowException_WhenAccountDoesNotExist() {
        UUID accountId = UUID.randomUUID();
        Account updatedDetails = new Account();
        when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> accountService.updateAccount(accountId, updatedDetails));
        verify(accountRepository, times(1)).findById(accountId);
    }

    @Test
    void partialUpdateAccount_ShouldPartiallyUpdateAndReturnAccount_WhenAccountExists() {
        UUID accountId = UUID.randomUUID();
        Account existingAccount = new Account();
        Account partialDetails = new Account();
        partialDetails.setBalance(500.0);

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(existingAccount));
        when(accountRepository.save(existingAccount)).thenReturn(existingAccount);

        Account result = accountService.partialUpdateAccount(accountId, partialDetails);

        assertEquals(500.0, existingAccount.getBalance());
        verify(accountRepository, times(1)).findById(accountId);
        verify(accountRepository, times(1)).save(existingAccount);
    }

    @Test
    void partialUpdateAccount_ShouldReturnNull_WhenAccountDoesNotExist() {
        UUID accountId = UUID.randomUUID();
        Account partialDetails = new Account();
        when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

        Account result = accountService.partialUpdateAccount(accountId, partialDetails);

        assertNull(result);
        verify(accountRepository, times(1)).findById(accountId);
    }

    @Test
    void deleteAccount_ShouldDeleteAccount_WhenAccountExists() {
        UUID accountId = UUID.randomUUID();
        when(accountRepository.existsById(accountId)).thenReturn(true);

        accountService.deleteAccount(accountId);

        verify(accountRepository, times(1)).existsById(accountId);
        verify(accountRepository, times(1)).deleteById(accountId);
    }

    @Test
    void deleteAccount_ShouldThrowException_WhenAccountDoesNotExist() {
        UUID accountId = UUID.randomUUID();
        when(accountRepository.existsById(accountId)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> accountService.deleteAccount(accountId));
        verify(accountRepository, times(1)).existsById(accountId);
    }

    @Test
    void deleteAllAccounts_ShouldDeleteAllAccounts() {
        accountService.deleteAllAccounts();

        verify(accountRepository, times(1)).deleteAll();
    }
}

