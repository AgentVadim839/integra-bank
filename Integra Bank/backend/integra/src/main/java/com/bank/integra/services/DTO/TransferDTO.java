package com.bank.integra.services.DTO;

import lombok.Data;

@Data
public class TransferDTO {
    private Integer senderId;
    private Integer recipientId;
    private Double amount;
}
