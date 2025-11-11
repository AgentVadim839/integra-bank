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

//TODO сделатьб кнопачки сос качиванием пдфки (если её нема - пересоздать), туда заходим по айди, но только если это запрос от пользователя получателя/атправителя, остальным - бан
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
