package com.account.listener;

import com.account.entity.Transaction;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class TransactionMessageListener {

    @RabbitListener(queues = "transaction.queue")
    public void handleTransactionCreation(Transaction transaction) {
        System.out.println("Received transaction: " + transaction);
        
    }
}
