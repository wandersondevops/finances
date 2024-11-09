package com.client.service;

import com.client.entity.Client;
import com.client.entity.Person;
import com.client.repository.ClientRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.transaction.Transactional;

import com.client.exception.ResourceNotFoundException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.lang.reflect.Field;
import java.util.Map;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class ClientService {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    public ClientService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public Client getClientDetails(UUID clientId) {
        String requestMessage = "GetClientById:" + clientId.toString();
        String clientJson = (String) rabbitTemplate.convertSendAndReceive("client.request.queue", requestMessage);

        // Parse the JSON response to Client object
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(clientJson, Client.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to parse client data from client-service", e);
        }
    }

    // Method to get all clients
    public List<Client> getAllClients() {
        return clientRepository.findAll();
    }

    // Method to get a client by ID
    public Optional<Client> getClientById(UUID clientId) {
        return clientRepository.findByClientId(clientId);
    }

    // Create a new client and publish the creation event
    private static final Logger logger = LoggerFactory.getLogger(ClientService.class);
    
    public Client createClient(Client client) {
        String encodedPassword = passwordEncoder.encode(client.getPassword());
        client.setPassword(encodedPassword);
        logger.info("Encoded Password for client ID " + client.getClientId() + ": " + encodedPassword);
        
        Client savedClient = clientRepository.save(client);
        rabbitTemplate.convertAndSend("client.exchange", "client.creation", savedClient);
        return savedClient;
    }

    public List<Client> createClients(List<Client> clients) {
        clients.forEach(client -> client.setPassword(passwordEncoder.encode(client.getPassword())));
        List<Client> savedClients = clientRepository.saveAll(clients);
    
        // Publish an event for each client created
        savedClients.forEach(savedClient -> 
            rabbitTemplate.convertAndSend("client.exchange", "client.creation", savedClient)
        );
    
        return savedClients;
    }    

    public Optional<Client> patchClient(UUID clientId, Map<String, Object> updates) {
        Client client = clientRepository.findByClientId(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with id: " + clientId));

        updates.forEach((key, value) -> {
            try {
                Field field;
                try {
                    // Try to get the field from the Client class first
                    field = Client.class.getDeclaredField(key);
                } catch (NoSuchFieldException e) {
                    // If not found in Client, look in the superclass Person
                    field = Person.class.getDeclaredField(key);
                }
                field.setAccessible(true);

                if ("password".equals(key)) {
                    value = passwordEncoder.encode((String) value); // Encrypt password if updating password
                }

                field.set(client, value);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                logger.error("Field update failed for key: " + key, e);
                throw new RuntimeException("Field update failed for key: " + key, e);
            }
        });

        Client updatedClient = clientRepository.save(client);

        // Publish an event to RabbitMQ for the partial update
        rabbitTemplate.convertAndSend("client.exchange", "client.partialUpdate", updatedClient);
        logger.info("Published partial update event to RabbitMQ for client ID: " + clientId);

        return Optional.of(updatedClient);
    }

    // Update an existing client and publish the update event
    public Client updateClient(UUID clientId, Client clientDetails) {
        Client client = clientRepository.findByClientId(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with id: " + clientId));

        client.setName(clientDetails.getName());
        client.setGender(clientDetails.getGender());
        client.setAge(clientDetails.getAge());
        client.setIdentification(clientDetails.getIdentification());
        client.setAddress(clientDetails.getAddress());
        client.setPhone(clientDetails.getPhone());
        client.setClientId(clientDetails.getClientId());
        if (clientDetails.getPassword() != null) {
            client.setPassword(passwordEncoder.encode(clientDetails.getPassword()));
        }        
        client.setStatus(clientDetails.isStatus());

        Client updatedClient = clientRepository.save(client);

        try {
            rabbitTemplate.convertAndSend("client.exchange", "client.update", updatedClient);
        } catch (Exception e) {
            logger.error("Failed to publish client update event", e);
        }
        
        return updatedClient;
    }

    // Delete a client and publish the deletion event

    @Transactional
    public void deleteClient(UUID clientId) {
        if (!clientRepository.existsByClientId(clientId)) {
            throw new ResourceNotFoundException("Client not found with id: " + clientId);
        }
        clientRepository.deleteByClientId(clientId);

        // Publish an event to RabbitMQ for client deletion
        rabbitTemplate.convertAndSend("client.exchange", "client.deletion", clientId);
    }

    public void deleteAllClients() {
        // Delete all clients from the repository
        clientRepository.deleteAll();
    
        // Publish an event to RabbitMQ indicating bulk deletion
        rabbitTemplate.convertAndSend("client.exchange", "client.bulkDeletion", "All clients have been deleted");
    }
    
}
