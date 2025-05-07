package com.bank.integra.entities.details;

import com.bank.integra.entities.person.User;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "transactions")
@Data
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_id")
    private Integer id;

    @Column(name = "amount")
    private Double balance;

    @Column(name = "timestamp")
    private LocalDateTime eventTimeStamp;

    @Column(name = "description")
    private String description;

    @ManyToOne
    @JoinColumn(name = "sender_id")
    private UserDetails sender;

    @ManyToOne
    @JoinColumn(name = "recipient_id")
    private UserDetails recipient;

    @Column(name = "idempotency_key", unique = true)
    private String idempotencyKey;
}
