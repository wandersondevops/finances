package com.client.repository;

import com.client.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ClientRepository extends JpaRepository<Client, String> {

    Optional<Client> findByClientId(UUID clientId);

    List<Client> findByStatus(boolean status);

    List<Client> findByPassword(String password);

    Optional<Client> findByName(String name);

    List<Client> findByNameContainingIgnoreCase(String name);

    long countByStatus(boolean status);

    boolean existsByClientId(UUID clientId);

    void deleteByClientId(UUID clientId);

    List<Client> findByStatusTrue();

    List<Client> findByStatusFalse();
}
