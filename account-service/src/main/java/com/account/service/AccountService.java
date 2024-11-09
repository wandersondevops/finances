package com.account.service;

import com.account.entity.Account;
import com.account.repository.AccountRepository;
import com.account.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;


    public List<Account> getAccountsByClientId(UUID clientId) {
        return accountRepository.findByClientId(clientId);
    }

    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    public Optional<Account> getAccountById(UUID accountId) {
        return accountRepository.findById(accountId);
    }

    public List<Account> createAccounts(List<Account> accounts) {
        return accountRepository.saveAll(accounts);
    }     

    public Account updateAccount(UUID accountId, Account accountDetails) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + accountId));

        account.setAccountType(accountDetails.getAccountType());
        account.setBalance(accountDetails.getBalance());

        return accountRepository.save(account);
    }

    public Account partialUpdateAccount(UUID accountId, Account accountDetails) {
        return accountRepository.findById(accountId)
                .map(account -> {
                    if (accountDetails.getAccountNumber() != null) {
                        account.setAccountNumber(accountDetails.getAccountNumber());
                    }
                    if (accountDetails.getAccountType() != null) {
                        account.setAccountType(accountDetails.getAccountType());
                    }
                    if (accountDetails.getBalance() != null) {
                        account.setBalance(accountDetails.getBalance());
                    }
                    return accountRepository.save(account);
                })
                .orElse(null);
    }

    public void deleteAllAccounts() {
        accountRepository.deleteAll();
    }    

    public void deleteAccount(UUID accountId) {
        if (!accountRepository.existsById(accountId)) {
            throw new ResourceNotFoundException("Account not found with id: " + accountId);
        }
        accountRepository.deleteById(accountId);
    }
}
