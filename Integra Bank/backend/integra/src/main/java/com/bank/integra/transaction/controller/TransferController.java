package com.bank.integra.transaction.controller;

import com.bank.integra.general.enums.PaymentValidationResponse;
import com.bank.integra.transaction.dto.TransferDTO;
import com.bank.integra.transaction.service.PaymentService;
import com.bank.integra.transaction.service.TransactionsService;
import com.bank.integra.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

//TODO Сделать отображение кнопачки скачать пдф транзакции
@RequestMapping("/user")
@Controller
public class TransferController {
    private final PaymentService paymentService;
    private final UserService userService;
    private final TransactionsService transactionsService;

    public TransferController(PaymentService paymentService, UserService userService, TransactionsService transactionsService) {
        this.paymentService = paymentService;
        this.userService = userService;
        this.transactionsService = transactionsService;
    }

    @PostMapping("/transfer")
    public String makeTransfer(@RequestParam Integer senderId,
                               @RequestParam Integer recipientId,
                               @RequestParam Double amount, Model model, RedirectAttributes redirectAttributes) {


        PaymentValidationResponse paymentValidationResponse = checkBeforePayment(senderId,recipientId,amount);
        if(!paymentValidationResponse.equals(PaymentValidationResponse.OK)) {
            redirectAttributes.addFlashAttribute("information", paymentValidationResponse.getDescription());
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

    //TODO Для проверок лучше попробовать оформить АОП в аннотацию.
    private PaymentValidationResponse checkBeforePayment(Integer senderId, Integer recipientId, Double amount) {
        if(PaymentService.checkIfUserNull(senderId, recipientId, userService)) return PaymentValidationResponse.INVALID_FORMAT;
        if(PaymentService.checkIfUserHasEnoughMoney(amount, userService.getUserDetailsByUserId(senderId))) return PaymentValidationResponse.NOT_ENOUGH_FUNDS;
        if(PaymentService.checkIfUserTheSameAsCurrent(senderId, recipientId)) return PaymentValidationResponse.ID_IS_SAME_AS_CURRENT;
        if(PaymentService.checkIfUserIsBanned(recipientId, userService)) return PaymentValidationResponse.USER_BANNED;
        return PaymentValidationResponse.OK; 
    }


    @PostMapping("confirm-transfer")
    public String confirmTransfer(@RequestParam Integer senderId,
                                  @RequestParam Integer recipientId,
                                  @RequestParam Double amount,
                                  @RequestParam UUID idempotencyKey, RedirectAttributes redirectAttributes) {
        try {
            paymentService.makePayment(senderId, recipientId, amount, idempotencyKey);
        } catch(RuntimeException e) {
            System.out.println("Duplicate of transaction.");
        }
        redirectAttributes.addFlashAttribute("information", "Transaction successful");
        return "redirect:/user/home";
    }
}
