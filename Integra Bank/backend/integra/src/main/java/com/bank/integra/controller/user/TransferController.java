package com.bank.integra.controller.user;

import com.bank.integra.services.DTO.TransferDTO;
import com.bank.integra.services.bank.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/user")
@Controller
public class TransferController {
    @Autowired
    private PaymentService paymentService;

    //TODO Страница подтверждения операции перевода запилить и валидацию!
    @PostMapping("/transfer")
    public String makeTransfer(@ModelAttribute TransferDTO transferDTO, Model model) {
        paymentService.makePayment(transferDTO.getSenderId(), transferDTO.getRecipientId(), transferDTO.getAmount(), model);
        return "redirect:/user/home";
    }
}
