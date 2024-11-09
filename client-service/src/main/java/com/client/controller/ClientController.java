package com.client.controller;

import com.client.entity.Client;
import com.client.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/clientes")
public class ClientController {

    @Autowired
    private ClientService clientService;

    @GetMapping
    public List<Client> getAllClients() {
        return clientService.getAllClients();
    }

    @GetMapping("/{clientId}")
    public ResponseEntity<Client> getClientById(@PathVariable UUID clientId) {
        Optional<Client> client = clientService.getClientById(clientId);
        return client.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<List<Client>> createClients(@RequestBody List<Client> clients) {
        List<Client> savedClients = clientService.createClients(clients);
        return ResponseEntity.ok(savedClients);
    }

    @PutMapping("/{clientId}")
    public ResponseEntity<Client> updateClient(@PathVariable UUID clientId, @RequestBody Client clientDetails) {
        Client updatedClient = clientService.updateClient(clientId, clientDetails);
        return updatedClient != null ? ResponseEntity.ok(updatedClient) : ResponseEntity.notFound().build();
    }

    @PatchMapping("/{clientId}")
    public ResponseEntity<Client> patchClient(@PathVariable UUID clientId, @RequestBody Map<String, Object> updates) {
        Optional<Client> updatedClient = clientService.patchClient(clientId, updates);
        return updatedClient.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAllClients() {
        clientService.deleteAllClients();
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{clientId}")
    public ResponseEntity<Void> deleteClient(@PathVariable UUID clientId) {
        clientService.deleteClient(clientId);
        return ResponseEntity.noContent().build();
    }
}
