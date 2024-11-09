package com.account.service;

import com.account.dto.ClientDTO;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Service
public class ClientServiceCommunicator {

    private final RabbitTemplate rabbitTemplate;

    @Autowired
    private RestTemplate restTemplate;

    public ClientServiceCommunicator(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public ClientDTO getClientDetails(UUID clientId) {

    ResponseEntity<ClientDTO> response = restTemplate.getForEntity("http://localhost:8080/clients/" + clientId, ClientDTO.class);
    return response.getBody();
}

}
