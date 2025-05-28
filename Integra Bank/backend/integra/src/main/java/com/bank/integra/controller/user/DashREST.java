package com.bank.integra.controller.user;

import com.bank.integra.dao.PdfRepository;
import com.bank.integra.entities.details.PdfReceipt;
import com.bank.integra.services.bank.TransactionsService;
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
    @Autowired
    private TransactionsService transactionsService;

    @Autowired
    private PdfRepository pdfRepository;

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
