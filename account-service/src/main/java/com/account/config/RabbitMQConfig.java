package com.account.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.account.service.ClientServiceCommunicator;

@Configuration
public class RabbitMQConfig {

    public static final String CLIENT_REQUEST_QUEUE = "client.request.queue";
    public static final String CLIENT_RESPONSE_QUEUE = "client.response.queue";

    @Bean
    public Queue clientRequestQueue() {
        return new Queue(CLIENT_REQUEST_QUEUE, true);
    }

    @Bean
    public Queue clientResponseQueue() {
        return new Queue(CLIENT_RESPONSE_QUEUE, false);
    }

    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public SimpleMessageListenerContainer messageListenerContainer(ConnectionFactory connectionFactory, MessageListenerAdapter listenerAdapter) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(CLIENT_RESPONSE_QUEUE);
        container.setMessageListener(listenerAdapter);
        return container;
    }

    @Bean
    public MessageListenerAdapter listenerAdapter(ClientServiceCommunicator clientServiceCommunicator) {
        MessageListenerAdapter listenerAdapter = new MessageListenerAdapter(clientServiceCommunicator, "handleClientResponse");
        listenerAdapter.setMessageConverter(jsonMessageConverter());
        return listenerAdapter;
    }
}
