package com.account.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AccountServiceConfig {

    public static final String ACCOUNT_EXCHANGE = "account.exchange";
    public static final String ACCOUNT_CREATION_ROUTING_KEY = "account.creation";
    public static final String ACCOUNT_UPDATE_ROUTING_KEY = "account.update";
    public static final String ACCOUNT_DELETION_ROUTING_KEY = "account.deletion";
    public static final String ACCOUNT_REQUEST_ROUTING_KEY = "account.request";
    public static final String ACCOUNT_RESPONSE_ROUTING_KEY = "account.response";

    public static final String TRANSACTION_EXCHANGE = "transaction.exchange";
    public static final String TRANSACTION_CREATION_ROUTING_KEY = "transaction.creation";
    public static final String TRANSACTION_QUEUE = "transaction.queue";

    @Bean
    public Queue transactionQueue() {
        return new Queue(TRANSACTION_QUEUE, true);
    }

    @Bean
    public DirectExchange transactionExchange() {
        return new DirectExchange(TRANSACTION_EXCHANGE);
    }

    @Bean
    public Binding transactionBinding(Queue transactionQueue, DirectExchange transactionExchange) {
        return BindingBuilder.bind(transactionQueue).to(transactionExchange).with(TRANSACTION_CREATION_ROUTING_KEY);
    }

    @Bean
    public Queue accountCreationQueue() {
        return new Queue("account.creation.queue", true);
    }

    @Bean
    public Queue accountUpdateQueue() {
        return new Queue("account.update.queue", true);
    }

    @Bean
    public Queue accountDeletionQueue() {
        return new Queue("account.deletion.queue", true);
    }

    @Bean
    public Queue accountRequestQueue() {
        return new Queue("account.request.queue", true);
    }

    @Bean
    public Queue accountResponseQueue() {
        return new Queue("account.response.queue", true);
    }

    @Bean
    public DirectExchange accountExchange() {
        return new DirectExchange(ACCOUNT_EXCHANGE);
    }

    @Bean
    public Binding accountCreationBinding(Queue accountCreationQueue, DirectExchange accountExchange) {
        return BindingBuilder.bind(accountCreationQueue).to(accountExchange).with(ACCOUNT_CREATION_ROUTING_KEY);
    }

    @Bean
    public Binding accountUpdateBinding(Queue accountUpdateQueue, DirectExchange accountExchange) {
        return BindingBuilder.bind(accountUpdateQueue).to(accountExchange).with(ACCOUNT_UPDATE_ROUTING_KEY);
    }

    @Bean
    public Binding accountDeletionBinding(Queue accountDeletionQueue, DirectExchange accountExchange) {
        return BindingBuilder.bind(accountDeletionQueue).to(accountExchange).with(ACCOUNT_DELETION_ROUTING_KEY);
    }

    @Bean
    public Binding accountRequestBinding(Queue accountRequestQueue, DirectExchange accountExchange) {
        return BindingBuilder.bind(accountRequestQueue).to(accountExchange).with(ACCOUNT_REQUEST_ROUTING_KEY);
    }

    @Bean
    public Binding accountResponseBinding(Queue accountResponseQueue, DirectExchange accountExchange) {
        return BindingBuilder.bind(accountResponseQueue).to(accountExchange).with(ACCOUNT_RESPONSE_ROUTING_KEY);
    }
}
