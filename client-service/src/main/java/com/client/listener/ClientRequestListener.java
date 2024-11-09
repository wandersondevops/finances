package com.client.listener;

import com.client.entity.Client;
import com.client.repository.ClientRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class ClientRequestListener {

    private final ClientRepository clientRepository;

    public ClientRequestListener(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @RabbitListener(queues = "${rabbitmq.client.request.queue}")
    public Client handleClientDataRequest(String clientIdString) {
        UUID clientId = UUID.fromString(clientIdString);


        Optional<Client> clientOpt = clientRepository.findByClientId(clientId);
        return clientOpt.orElse(null);
    }
}

