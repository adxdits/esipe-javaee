package com.bank.model;

/* © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. */

import java.util.ArrayList;
import java.util.List;

/**
 * Client entity representing a bank customer.
 * 
 * Client entity representing a bank customer.
 */
public class Client {
    
    private Long id;
    private String name;
    private String email;
    private List<Account> accounts;
    
    public Client() {
        accounts = new ArrayList<>();
    }

    public Client(String name, String email) {
        this();
        this.name = name;
        this.email = email;
    }

    public Client(Long id, String name, String email) {
        this(name, email);
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<Account> accounts) {
        this.accounts = accounts == null ? new ArrayList<>() : accounts;
    }

    public void addAccount(Account account) {
        if (account != null) {
            accounts.add(account);
            account.setClientId(id);
        }
    }

    public void removeAccount(Account account) {
        if (accounts != null) {
            accounts.remove(account);
        }
    }

    @Override
    public String toString() {
        return "Client{id=" + id + ", name='" + name + "', email='" + email
                + "', accounts=" + (accounts == null ? 0 : accounts.size()) + "}";
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Client)) {
            return false;
        }
        Client client = (Client) other;
        return id != null && id.equals(client.id);
    }

    @Override
    public int hashCode() {
        return id == null ? 0 : id.hashCode();
    }
}

// Made with Bob
