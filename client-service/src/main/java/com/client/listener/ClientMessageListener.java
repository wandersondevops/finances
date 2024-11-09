package com.client.listener;

import com.client.entity.Client;
import com.client.repository.ClientRepository;
import com.client.service.ClientService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Optional;
import java.util.UUID;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ClientMessageListener {

    @Autowired
    private ClientService clientService;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @RabbitListener(queues = "client.request.queue")
    public String handleGetClientByIdRequest(String message) {
        if (message.startsWith("GetClientById:")) {
            UUID clientId = UUID.fromString(message.split(":")[1]);
            Client client = clientRepository.findByClientId(clientId).orElse(null);

            try {
                return objectMapper.writeValueAsString(client);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Failed to serialize client data", e);
            }
        }
        return null;
    }

    @RabbitListener(queues = "client.request.queue")
    public String handleClientRequest(String message) {
        if (message.startsWith("GetClientById:")) {
            String clientId = message.split(":")[1];
            Optional<Client> client = clientService.getClientById(UUID.fromString(clientId));
            return client.map(Client::toString).orElse(null);
        }
        return null;
    }
}
