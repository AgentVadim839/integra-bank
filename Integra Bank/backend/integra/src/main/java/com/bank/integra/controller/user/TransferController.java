package com.bank.integra.controller.user;

import com.bank.integra.services.DTO.TransferDTO;
import com.bank.integra.services.bank.PaymentService;
import com.bank.integra.services.person.TransactionsService;
import com.bank.integra.services.person.UserService;
import jakarta.annotation.PostConstruct;
import jakarta.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.UUID;

@RequestMapping("/user")
@Controller
public class TransferController {
    @Autowired
    private PaymentService paymentService;

    @Autowired
    private UserService userService;

    @Autowired
    private TransactionsService transactionsService;

    //TODO Запилить и валидацию!
    @PostMapping("/transfer")
    public String makeTransfer(@RequestParam Integer senderId,
                               @RequestParam Integer recipientId,
                               @RequestParam Double amount, Model model) {
        TransferDTO transferDTO = new TransferDTO(senderId, recipientId, amount,
                    userService.getUserDetailsByUserId(senderId).getBalance(),
                    userService.getUserDetailsByUserId(recipientId).getFirstName(),
                    userService.getUserDetailsByUserId(recipientId).getLastName());

        model.addAttribute("transferData", transferDTO);
        model.addAttribute("transactionOn", "true");
        return "confirmPayment";
    }


    @PostMapping("confirm-transfer")
    public String confirmTransfer(@RequestParam Integer senderId,
                                  @RequestParam Integer recipientId,
                                  @RequestParam Double amount,
                                  @RequestParam UUID idempotencyKey, Model model) {
        try {
            paymentService.makePayment(senderId, recipientId, amount, idempotencyKey, model);
        } catch(RuntimeException e) {
            System.out.println("Duplicate of transaction.");
        }
        return "redirect:/user/home?transactionSuccess=true";
    }
}
