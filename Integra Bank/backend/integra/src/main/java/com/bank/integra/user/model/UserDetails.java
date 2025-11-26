package com.bank.integra.user.model;

import com.bank.integra.transaction.model.Transaction;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "user_details")
public class UserDetails {

    @Id
    @Column(name = "user_id")
    private Integer userId;

    // TODO трай кетч с ретраем (старая версия - обновите)
    @Version
    @Column(name = "version")
    private Long version;

    // TODO меняй дабл на Big_Decimal))) ВЕЗДЕ)))
    @Column(name = "balance")
    private Double balance;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "transaction_history")
    private String transactionHistory;

    @Column(name = "email")
    private String email;

    @OneToMany(mappedBy = "sender")
    private List<Transaction> sentTransactions;

    @OneToMany(mappedBy = "recipient")
    private List<Transaction> receivedTransactions;

    public UserDetails(Integer userId, Double balance, String firstName, String lastName, String transactionHistory, String email) {
        this.userId = userId;
        this.balance = balance;
        this.firstName = firstName;
        this.lastName = lastName;
        this.transactionHistory = transactionHistory;
        this.email = email;
    }

    public UserDetails() {

    }

    // Getters and setters


    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public List<Transaction> getSentTransactions() {
        return sentTransactions;
    }

    public void setSentTransactions(List<Transaction> sentTransactions) {
        this.sentTransactions = sentTransactions;
    }

    public List<Transaction> getReceivedTransactions() {
        return receivedTransactions;
    }

    public void setReceivedTransactions(List<Transaction> receivedTransactions) {
        this.receivedTransactions = receivedTransactions;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getTransactionHistory() {
        return transactionHistory;
    }

    public void setTransactionHistory(String transactionHistory) {
        this.transactionHistory = transactionHistory;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
