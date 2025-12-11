package com.bank.integra.transaction.dto;

import lombok.Data;

import java.math.BigDecimal;

// TODO Validation for fields

@Data
public class TransferDTO {
    private Integer senderId;
    private Integer recipientId;
    private BigDecimal amount;
    private BigDecimal currentBalance;
    private String recipientName;
    private String recipientLastName;

    public TransferDTO(Integer senderId, Integer recipientId, BigDecimal amount, BigDecimal currentBalance, String recipientName, String recipientLastName) {
        this.senderId = senderId;
        this.recipientId = recipientId;
        this.amount = amount;
        this.currentBalance = currentBalance;
        this.recipientName = recipientName;
        this.recipientLastName = recipientLastName;
    }

    @Override
    public String toString() {
        return "TransferDTO{" +
                "senderId=" + senderId +
                ", recipientId=" + recipientId +
                ", amount=" + amount +
                ", currentBalance=" + currentBalance +
                ", recipientName='" + recipientName + '\'' +
                ", recipientLastName='" + recipientLastName + '\'' +
                '}';
    }
}
