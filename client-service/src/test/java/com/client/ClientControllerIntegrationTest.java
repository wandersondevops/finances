package com.client;

import com.client.controller.ClientController;
import com.client.entity.Client;
import com.client.service.ClientService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ClientController.class)
class ClientControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ClientService clientService;

    private Client client;
    private UUID clientId;

    @BeforeEach
    void setUp() {
        clientId = UUID.randomUUID();
        client = new Client();
        client.setClientId(clientId);
        client.setName("John Doe");
        client.setPassword("password");
    }

    @Test
    void getAllClients_ShouldReturnClientList() throws Exception {
        List<Client> clients = Collections.singletonList(client);
        when(clientService.getAllClients()).thenReturn(clients);

        mockMvc.perform(get("/clientes")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is(client.getName())));
    }

    @Test
    void getClientById_ShouldReturnClient_WhenClientExists() throws Exception {
        when(clientService.getClientById(clientId)).thenReturn(Optional.of(client));

        mockMvc.perform(get("/clientes/{clientId}", clientId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(client.getName())));
    }

    @Test
    void getClientById_ShouldReturnNotFound_WhenClientDoesNotExist() throws Exception {
        when(clientService.getClientById(clientId)).thenReturn(Optional.empty());

        mockMvc.perform(get("/clientes/{clientId}", clientId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void createClients_ShouldReturnCreatedClients() throws Exception {
        List<Client> clients = Collections.singletonList(client);
        when(clientService.createClients(any())).thenReturn(clients);

        mockMvc.perform(post("/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content("[{\"name\": \"John Doe\", \"password\": \"password\"}]"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is(client.getName())));
    }

    @Test
    void updateClient_ShouldReturnUpdatedClient_WhenClientExists() throws Exception {
        client.setName("Updated Name");
        when(clientService.updateClient(eq(clientId), any(Client.class))).thenReturn(client);

        mockMvc.perform(put("/clientes/{clientId}", clientId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"Updated Name\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Updated Name")));
    }

    @Test
    void updateClient_ShouldReturnNotFound_WhenClientDoesNotExist() throws Exception {
        when(clientService.updateClient(eq(clientId), any(Client.class))).thenThrow(new NoSuchElementException());

        mockMvc.perform(put("/clientes/{clientId}", clientId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"Updated Name\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void patchClient_ShouldReturnUpdatedClient_WhenClientExists() throws Exception {
        client.setName("Patched Name");
        Map<String, Object> updates = Map.of("name", "Patched Name");
        when(clientService.patchClient(eq(clientId), eq(updates))).thenReturn(Optional.of(client));

        mockMvc.perform(patch("/clientes/{clientId}", clientId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"Patched Name\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Patched Name")));
    }

    @Test
    void patchClient_ShouldReturnNotFound_WhenClientDoesNotExist() throws Exception {
        Map<String, Object> updates = Map.of("name", "Patched Name");
        when(clientService.patchClient(eq(clientId), eq(updates))).thenReturn(Optional.empty());

        mockMvc.perform(patch("/clientes/{clientId}", clientId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"Patched Name\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteClient_ShouldReturnNoContent_WhenClientExists() throws Exception {
        doNothing().when(clientService).deleteClient(clientId);

        mockMvc.perform(delete("/clientes/{clientId}", clientId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteAllClients_ShouldReturnNoContent() throws Exception {
        doNothing().when(clientService).deleteAllClients();

        mockMvc.perform(delete("/clientes")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }
}

