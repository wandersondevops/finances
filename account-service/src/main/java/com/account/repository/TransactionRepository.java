package com.account.repository;

import com.account.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
    
    @Query("SELECT t FROM Transaction t WHERE t.accountId = :accountId AND t.transactionDate BETWEEN :startDate AND :endDate")
    List<Transaction> findByAccountIdAndDateRange(
            @Param("accountId") UUID accountId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    List<Transaction> findByAccountIdAndTransactionDateBetween(UUID accountId, LocalDateTime startDate, LocalDateTime endDate);
}
