package com.bank.service;

/* © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. */

import com.bank.model.Client;
import com.bank.model.Account;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Service class for managing clients.
 * Uses in-memory storage (will be replaced with database in Lab 3).
 * 
 * TODO: Implement all CRUD operations:
 * 1. findAll() - retrieve all clients
 * 2. findById(Long id) - find client by ID
 * 3. create(Client client) - add new client
 * 4. update(Client client) - update existing client
 * 5. delete(Long id) - remove client
 * 6. findByName(String name) - search clients by name
 */
public class ClientService {
    
    // In-memory storage
    private final Map<Long, Client> clients = new ConcurrentHashMap<>();
    private final AtomicLong nextId = new AtomicLong(1L);
    
    /**
     * Retrieve all clients.
     * 
     * TODO: Implement this method
     * Hint: Use clients.values() and convert to List
     * 
     * @return List of all clients
     */
    public List<Client> findAll() {
        return new ArrayList<>(clients.values());
    }
    
    /**
     * Find a client by ID.
     * 
     * TODO: Implement this method
     * Hint: Use clients.get(id)
     * 
     * @param id The client ID
     * @return The client if found, null otherwise
     */
    public Client findById(Long id) {
        return clients.get(id);
    }
    
    /**
     * Create a new client.
     * 
     * TODO: Implement this method
     * Steps:
     * 1. Generate new ID using nextId++
     * 2. Set the ID on the client
     * 3. Add client to the map
     * 4. Return the created client
     * 
     * @param client The client to create
     * @return The created client with ID set
     */
    public Client create(Client client) {
        client.setId(nextId.getAndIncrement());
        clients.put(client.getId(), client);
        return client;
    }
    
    /**
     * Update an existing client.
     * 
     * TODO: Implement this method
     * Steps:
     * 1. Check if client exists
     * 2. Update the client in the map
     * 3. Return the updated client
     * 
     * @param client The client to update
     * @return The updated client, or null if not found
     */
    public Client update(Client client) {
        if (client == null || client.getId() == null || !clients.containsKey(client.getId())) {
            return null;
        }
        clients.put(client.getId(), client);
        return client;
    }
    
    /**
     * Delete a client by ID.
     * 
     * TODO: Implement this method
     * Hint: Use clients.remove(id)
     * 
     * @param id The client ID to delete
     * @return true if deleted, false if not found
     */
    public boolean delete(Long id) {
        return clients.remove(id) != null;
    }
    
    /**
     * Search clients by name (partial match, case-insensitive).
     * 
     * TODO: Implement this method
     * Hint: Use stream() and filter() with contains()
     * 
     * @param name The name to search for
     * @return List of matching clients
     */
    public List<Client> findByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return findAll();
        }
        String query = name.trim().toLowerCase(Locale.ROOT);
        return clients.values().stream()
                .filter(client -> client.getName() != null
                        && client.getName().toLowerCase(Locale.ROOT).contains(query))
                .collect(java.util.stream.Collectors.toList());
    }
    
    /**
     * Add an account to a client.
     * 
     * TODO: Implement this method (optional)
     * 
     * @param clientId The client ID
     * @param account The account to add
     * @return true if added, false if client not found
     */
    public boolean addAccount(Long clientId, Account account) {
        Client client = clients.get(clientId);
        if (client == null || account == null) {
            return false;
        }
        client.addAccount(account);
        return true;
    }
    
    /**
     * Get the total number of clients.
     * 
     * @return The number of clients
     */
    public int count() {
        return clients.size();
    }

    public void initializeSampleData() {
        Client jean = create(new Client("Jean Dupont", "jean.dupont@example.com"));
        Client marie = create(new Client("Marie Martin", "marie.martin@example.com"));
        Client luc = create(new Client("Luc Bernard", "luc.bernard@example.com"));
        create(new Client("Sophie Leroy", "sophie.leroy@example.com"));
        Client claire = create(new Client("Claire Moreau", "claire.moreau@example.com"));

        addAccount(jean.getId(), new Account("FR7612345678901234567890123", 2000.0, "CHECKING", jean.getId()));
        addAccount(jean.getId(), new Account("FR7698765432109876543210987", 4500.0, "SAVINGS", jean.getId()));
        addAccount(marie.getId(), new Account("FR7611111111111111111111111", 2500.0, "CHECKING", marie.getId()));
        addAccount(luc.getId(), new Account("FR7622222222222222222222222", 3000.0, "CHECKING", luc.getId()));
        addAccount(luc.getId(), new Account("FR7633333333333333333333333", 10000.0, "SAVINGS", luc.getId()));
        addAccount(claire.getId(), new Account("FR7644444444444444444444444", 750.0, "CHECKING", claire.getId()));
    }
}

// Made with Bob
