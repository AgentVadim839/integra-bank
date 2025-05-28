package com.bank.integra.dao;

import com.bank.integra.entities.details.PdfReceipt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PdfRepository extends JpaRepository<PdfReceipt, Integer> {
    Optional<PdfReceipt> findByTransactionId(String id);

}
