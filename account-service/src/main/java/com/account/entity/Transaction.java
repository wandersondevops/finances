package com.account.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import jakarta.persistence.Column;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID transactionId;

    @Column(nullable = false)
    private UUID clientId;

    @Column(nullable = false)
    private UUID accountId;

    @Column(nullable = false)
    private double amount;

    @Column(nullable = false)
    private boolean credit;

    @Column(nullable = false)
    private LocalDateTime transactionDate;

    public void setCredit(boolean credit) {
        this.credit = credit;
    }
}
