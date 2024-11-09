package com.client.service;


import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClientServiceCommunicator {

    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public ClientServiceCommunicator(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public String requestClientData(String clientId) {
        rabbitTemplate.convertAndSend("client.request.queue", clientId);
        // Optional: you could add code to handle synchronous replies if configured
        return "Message sent to client.request.queue with clientId: " + clientId;
    }
}

