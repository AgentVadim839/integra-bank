package com.bank.integra.transaction.controller;

import com.bank.integra.transaction.dto.TransferDTO;
import com.bank.integra.transaction.service.PaymentService;
import com.bank.integra.transaction.service.TransactionsService;
import com.bank.integra.transaction.validation.PaymentValidation;
import com.bank.integra.transaction.validation.PaymentValidationResponseDTO;
import com.bank.integra.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.UUID;

//TODO Make "download pdf receipt" view button
@RequestMapping("/user")
@Controller
@Slf4j
public class TransferController {
    private final PaymentService paymentService;
    private final UserService userService;
    private final TransactionsService transactionsService;
    private final PaymentValidation paymentValidation;

    public TransferController(PaymentService paymentService, UserService userService, TransactionsService transactionsService, PaymentValidation paymentValidation) {
        this.paymentService = paymentService;
        this.userService = userService;
        this.transactionsService = transactionsService;
        this.paymentValidation = paymentValidation;
    }

    @PostMapping("/transfer")
    public String makeTransfer(Authentication authentication, @RequestParam Integer recipientId,
                               @RequestParam BigDecimal amount, Model model, RedirectAttributes redirectAttributes) {

        Integer senderId = Integer.parseInt(authentication.getName());

        PaymentValidationResponseDTO validatorAnswer = paymentValidation.validatePayment(senderId, recipientId, amount);
        if(!validatorAnswer.isValid()) {
            redirectAttributes.addFlashAttribute("information", validatorAnswer.getMessage());
            return "redirect:/user/home";
        }

        TransferDTO transferDTO = new TransferDTO(senderId, recipientId, amount,
                    userService.getUserDetailsByUserId(senderId).getBalance(),
                    userService.getUserDetailsByUserId(recipientId).getFirstName(),
                    userService.getUserDetailsByUserId(recipientId).getLastName());

        model.addAttribute("transferData", transferDTO);
        model.addAttribute("transactionOn", "true");
        return "confirmPayment";
    }



    @PostMapping("confirm-transfer")
    public String confirmTransfer(Authentication authentication,
                                  @RequestParam Integer recipientId,
                                  @RequestParam BigDecimal amount,
                                  @RequestParam UUID idempotencyKey, RedirectAttributes redirectAttributes) {
        try {
            Integer senderId = Integer.parseInt(authentication.getName());
            paymentService.makePayment(senderId, recipientId, amount, idempotencyKey);
            redirectAttributes.addFlashAttribute("information", "Transaction successful");
        } catch (ObjectOptimisticLockingFailureException y) {
            redirectAttributes.addFlashAttribute("information", "The system is overloaded. Please, try again.");
        } catch(RuntimeException e) {
            redirectAttributes.addFlashAttribute("information", "Transaction error.");
            log.error("Controller confirmTransfer wasn't successful.");
        }

        return "redirect:/user/home";
    }
}
