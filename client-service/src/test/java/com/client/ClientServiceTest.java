package com.client;

import com.client.entity.Client;
import com.client.entity.Person;
import com.client.exception.ResourceNotFoundException;
import com.client.repository.ClientRepository;
import com.client.service.ClientService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ClientServiceTest {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private ClientService clientService;

    private Client client;
    private UUID clientId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        clientId = UUID.randomUUID();
        client = new Client();
        client.setClientId(clientId);
        client.setName("John Doe");
        client.setPassword("password");
    }

    @Test
    void getClientDetails_ShouldReturnClientDetails() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        String clientJson = objectMapper.writeValueAsString(client);
        when(rabbitTemplate.convertSendAndReceive("client.request.queue", "GetClientById:" + clientId)).thenReturn(clientJson);

        Client retrievedClient = clientService.getClientDetails(clientId);

        assertNotNull(retrievedClient);
        assertEquals(client.getName(), retrievedClient.getName());
    }

    @Test
    void getAllClients_ShouldReturnAllClients() {
        List<Client> clients = Arrays.asList(client);
        when(clientRepository.findAll()).thenReturn(clients);

        List<Client> allClients = clientService.getAllClients();

        assertEquals(1, allClients.size());
        assertEquals(client.getName(), allClients.get(0).getName());
    }

    @Test
    void getClientById_ShouldReturnClient_WhenClientExists() {
        when(clientRepository.findByClientId(clientId)).thenReturn(Optional.of(client));

        Optional<Client> retrievedClient = clientService.getClientById(clientId);

        assertTrue(retrievedClient.isPresent());
        assertEquals(client.getName(), retrievedClient.get().getName());
    }

    @Test
    void createClient_ShouldEncodePasswordAndSaveClient() {
        String encodedPassword = "encodedPassword";
        when(passwordEncoder.encode("password")).thenReturn(encodedPassword);
        when(clientRepository.save(client)).thenReturn(client);

        Client createdClient = clientService.createClient(client);

        assertEquals(encodedPassword, createdClient.getPassword());
        verify(rabbitTemplate).convertAndSend("client.exchange", "client.creation", createdClient);
    }

    @Test
    void createClients_ShouldEncodePasswordsAndSaveClients() {
        Client client2 = new Client();
        client2.setPassword("password2");
        List<Client> clients = Arrays.asList(client, client2);

        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(clientRepository.saveAll(clients)).thenReturn(clients);

        List<Client> createdClients = clientService.createClients(clients);

        assertEquals(2, createdClients.size());
        verify(rabbitTemplate, times(2)).convertAndSend(eq("client.exchange"), eq("client.creation"), any(Client.class));
    }

    @Test
    void patchClient_ShouldUpdateFieldsAndSaveClient() {
        client.setAddress("Old Address");
        Map<String, Object> updates = new HashMap<>();
        updates.put("address", "New Address");

        when(clientRepository.findByClientId(clientId)).thenReturn(Optional.of(client));
        when(clientRepository.save(client)).thenReturn(client);

        Optional<Client> updatedClient = clientService.patchClient(clientId, updates);

        assertTrue(updatedClient.isPresent());
        assertEquals("New Address", updatedClient.get().getAddress());
        verify(rabbitTemplate).convertAndSend("client.exchange", "client.partialUpdate", updatedClient.get());
    }

    @Test
    void updateClient_ShouldUpdateClientDetails() {
        Client updatedDetails = new Client();
        updatedDetails.setName("Updated Name");
        updatedDetails.setPassword("newPassword");

        when(clientRepository.findByClientId(clientId)).thenReturn(Optional.of(client));
        when(passwordEncoder.encode("newPassword")).thenReturn("encodedNewPassword");
        when(clientRepository.save(client)).thenReturn(client);

        Client updatedClient = clientService.updateClient(clientId, updatedDetails);

        assertEquals("Updated Name", updatedClient.getName());
        assertEquals("encodedNewPassword", updatedClient.getPassword());
        verify(rabbitTemplate).convertAndSend("client.exchange", "client.update", updatedClient);
    }

    @Test
    void deleteClient_ShouldDeleteClientAndPublishEvent() {
        when(clientRepository.existsByClientId(clientId)).thenReturn(true);
        doNothing().when(clientRepository).deleteByClientId(clientId);

        clientService.deleteClient(clientId);

        verify(clientRepository).deleteByClientId(clientId);
        verify(rabbitTemplate).convertAndSend("client.exchange", "client.deletion", clientId);
    }

    @Test
    void deleteAllClients_ShouldDeleteAllClientsAndPublishEvent() {
        doNothing().when(clientRepository).deleteAll();

        clientService.deleteAllClients();

        verify(clientRepository).deleteAll();
        verify(rabbitTemplate).convertAndSend("client.exchange", "client.bulkDeletion", "All clients have been deleted");
    }

    @Test
    void updateClient_ShouldThrowException_WhenClientNotFound() {
        UUID nonExistentClientId = UUID.randomUUID();
        when(clientRepository.findByClientId(nonExistentClientId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> clientService.updateClient(nonExistentClientId, client));
    }
}

