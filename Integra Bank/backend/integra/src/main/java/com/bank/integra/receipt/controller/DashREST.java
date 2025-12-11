package com.bank.integra.receipt.controller;

import com.bank.integra.receipt.model.PdfReceipt;
import com.bank.integra.receipt.repository.PdfRepository;
import com.bank.integra.transaction.service.TransactionsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Optional;

// TODO Code to implement the functionality of creating download buttons for PDF files (if they do not exist), downloading them from a specific ID if it exists, but only allowing access to the file if the request is from the sender or recipient. For other users, block access.
@RestController
@RequestMapping("/user")
public class DashREST {
    private final TransactionsService transactionsService;
    private final PdfRepository pdfRepository;

    public DashREST(TransactionsService transactionsService, PdfRepository pdfRepository) {
        this.transactionsService = transactionsService;
        this.pdfRepository = pdfRepository;
    }

    @GetMapping("/transactions/{id}")
    public List<Map<String, Object>> showPrint(@PathVariable Integer id) {
        return transactionsService.getFormattedTransactionsForUserThreeRecent(id);
    }

    @GetMapping("/pdfs/{id}")
    public ResponseEntity<byte[]> viewPdf(@PathVariable Integer id) {
        Optional<PdfReceipt> optionalPdf = pdfRepository.findByTransactionId(id+"");
        if (optionalPdf.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        byte[] pdfData = optionalPdf.get().getPdfData();

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=receipt_" + id + ".pdf")
                .body(pdfData);
    }

}
