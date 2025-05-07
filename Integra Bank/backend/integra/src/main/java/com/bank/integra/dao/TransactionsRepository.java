package com.bank.integra.dao;

import com.bank.integra.entities.details.Transaction;
import com.bank.integra.entities.details.UserDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TransactionsRepository extends JpaRepository<Transaction, Integer> {
    List<Transaction> findBySender(UserDetails sender);
    List<Transaction> findByRecipient(UserDetails recipient);
    boolean existsByIdempotencyKey(String idempotencyKey);
}
