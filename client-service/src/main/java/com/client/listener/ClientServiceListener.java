package com.client.listener;

import java.util.UUID;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.client.entity.Client;
import com.client.repository.ClientRepository;

@Service
public class ClientServiceListener {

    @Autowired
    private ClientRepository clientRepository;

    @RabbitListener(queues = "client.request.queue")
    public Client handleClientRequest(UUID clientId) {
        return clientRepository.findByClientId(clientId).orElse(null);
    }
}

