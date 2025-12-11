package com.bank.integra.transaction.model;

import com.bank.integra.user.model.UserDetails;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Data
public class Transaction implements Comparable<Transaction> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_id")
    private Integer id;

    @Column(name = "amount")
    private BigDecimal balance;

    @Column(name = "timestamp")
    private LocalDateTime eventTimeStamp;

    @Column(name = "description")
    private String description;

    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = true)
    private UserDetails sender;

    @ManyToOne
    @JoinColumn(name = "recipient_id", nullable = true)
    private UserDetails recipient;


    @Column(name = "idempotency_key", unique = true)
    private String idempotencyKey;

    @Override
    public int compareTo(Transaction other) {
        return other.eventTimeStamp.compareTo(this.eventTimeStamp);
    }
}
