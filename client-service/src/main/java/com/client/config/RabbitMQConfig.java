package com.client.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.client.listener.ClientResponseListener;

@Configuration
public class RabbitMQConfig {

    public static final String CLIENT_EXCHANGE = "client.exchange";
    public static final String CLIENT_CREATION_ROUTING_KEY = "client.creation";
    public static final String CLIENT_UPDATE_ROUTING_KEY = "client.update";
    public static final String CLIENT_DELETION_ROUTING_KEY = "client.deletion";
    public static final String CLIENT_QUEUE = "client.queue";
    public static final String CLIENT_REQUEST_QUEUE = "client.request.queue";

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter());
        return rabbitTemplate;
    }

    @Bean
    public Queue clientQueue() {
        return new Queue(CLIENT_QUEUE, true);
    }

    @Bean
    public Queue clientRequestQueue() {
        return new Queue(CLIENT_REQUEST_QUEUE, true);
    }

    @Bean
    public Queue clientResponseQueue() {
        return new Queue("client.response.queue", false);
    }

    @Bean
    public DirectExchange clientExchange() {
        return new DirectExchange(CLIENT_EXCHANGE);
    }

    @Bean
    public Binding clientCreationBinding(Queue clientQueue, DirectExchange clientExchange) {
        return BindingBuilder.bind(clientQueue).to(clientExchange).with(CLIENT_CREATION_ROUTING_KEY);
    }

    @Bean
    public Binding clientUpdateBinding(Queue clientQueue, DirectExchange clientExchange) {
        return BindingBuilder.bind(clientQueue).to(clientExchange).with(CLIENT_UPDATE_ROUTING_KEY);
    }

    @Bean
    public Binding clientDeletionBinding(Queue clientQueue, DirectExchange clientExchange) {
        return BindingBuilder.bind(clientQueue).to(clientExchange).with(CLIENT_DELETION_ROUTING_KEY);
    }

    @Bean
    public SimpleMessageListenerContainer listenerContainer(ConnectionFactory connectionFactory,
                                                            MessageListenerAdapter listenerAdapter) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames("client.response.queue");
        container.setMessageListener(listenerAdapter);
        return container;
    }

    @Bean
    public MessageListenerAdapter listenerAdapter(ClientResponseListener receiver) {
        return new MessageListenerAdapter(receiver, "handleMessage");
    }
}
