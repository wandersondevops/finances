package com.account.repository;

import com.account.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
import java.util.List;

@Repository
public interface AccountRepository extends JpaRepository<Account, UUID> {
    
    List<Account> findByClientId(UUID clientId);
}

