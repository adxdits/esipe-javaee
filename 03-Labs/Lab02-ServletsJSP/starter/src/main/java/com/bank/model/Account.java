package com.bank.model;

/* © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. */

/**
 * Account entity representing a bank account.
 * 
 * Account entity representing a bank account.
 */
public class Account {
    
    private Long id;
    private String number;
    private double balance;
    private String type; // CHECKING or SAVINGS
    private Long clientId;
    
    public Account() {
    }

    public Account(String number, double balance, String type, Long clientId) {
        this.number = number;
        this.balance = balance;
        this.type = type;
        this.clientId = clientId;
    }

    public Account(Long id, String number, double balance, String type, Long clientId) {
        this(number, balance, type, clientId);
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public void deposit(double amount) {
        if (amount > 0) {
            balance += amount;
        }
    }

    public boolean withdraw(double amount) {
        if (amount <= 0 || amount > balance) {
            return false;
        }
        balance -= amount;
        return true;
    }

    @Override
    public String toString() {
        return "Account{id=" + id + ", number='" + number + "', balance=" + balance
                + ", type='" + type + "', clientId=" + clientId + "}";
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Account)) {
            return false;
        }
        Account account = (Account) other;
        return id != null && id.equals(account.id);
    }

    @Override
    public int hashCode() {
        return id == null ? 0 : id.hashCode();
    }
}

// Made with Bob
