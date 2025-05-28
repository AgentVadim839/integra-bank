package com.bank.integra.entities.details;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name="pdf_receipts")
@Data
public class PdfReceipt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "transaction_id")
    private String transactionId;

    @Column(name = "generated_at")
    private LocalDateTime generatedAt;

    @Lob
    @Column(name = "pdf_data", columnDefinition = "BLOB")
    private byte[] pdfData;

    public PdfReceipt(String transactionId, LocalDateTime generatedAt, byte[] pdfData) {
        this.transactionId = transactionId;
        this.generatedAt = generatedAt;
        this.pdfData = pdfData;
    }

    public PdfReceipt() {}
}
