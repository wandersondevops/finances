package com.account.controller;

import com.account.entity.Account;
import com.account.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/cuentas")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @GetMapping
    public List<Account> getAllAccounts() {
        return accountService.getAllAccounts();
    }

    @GetMapping("/{accountId}")
    public ResponseEntity<Account> getAccountById(@PathVariable UUID accountId) {
        Optional<Account> account = accountService.getAccountById(accountId);
        return account.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<List<Account>> createAccounts(@RequestBody List<Account> accounts) {
        List<Account> createdAccounts = accountService.createAccounts(accounts);
        return ResponseEntity.ok(createdAccounts);
    }

    @PutMapping("/{accountId}")
    public ResponseEntity<Account> updateAccount(@PathVariable UUID accountId, @RequestBody Account accountDetails) {
        Account updatedAccount = accountService.updateAccount(accountId, accountDetails);
        return updatedAccount != null ? ResponseEntity.ok(updatedAccount) : ResponseEntity.notFound().build();
    }

    @PatchMapping("/{accountId}")
    public ResponseEntity<Account> partialUpdateAccount(@PathVariable UUID accountId, @RequestBody Account accountDetails) {
        Account updatedAccount = accountService.partialUpdateAccount(accountId, accountDetails);
        return updatedAccount != null ? ResponseEntity.ok(updatedAccount) : ResponseEntity.notFound().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAllAccounts() {
        accountService.deleteAllAccounts();
        return ResponseEntity.noContent().build();
    }    

    @DeleteMapping("/{accountId}")
    public ResponseEntity<Void> deleteAccount(@PathVariable UUID accountId) {
        accountService.deleteAccount(accountId);
        return ResponseEntity.noContent().build();
    }
}
