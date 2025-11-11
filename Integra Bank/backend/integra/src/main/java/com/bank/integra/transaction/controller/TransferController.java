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

    //TODO case 1, 0, 3 - дриндж, переведи в енумы с константными значениями.
    @PostMapping("/transfer")
    public String makeTransfer(@RequestParam Integer senderId,
                               @RequestParam Integer recipientId,
                               @RequestParam Double amount, Model model, RedirectAttributes redirectAttributes) {


        switch(checkBeforePayment(senderId,recipientId,amount)){
            case 0:
                redirectAttributes.addFlashAttribute("information", PaymentValidationResponse.INVALID_FORMAT.getDescription());
                return "redirect:/user/home";
            case 1:
                redirectAttributes.addFlashAttribute("information", PaymentValidationResponse.NOT_ENOUGH_FUNDS.getDescription());
                return "redirect:/user/home";
            case 2:
                redirectAttributes.addFlashAttribute("information", PaymentValidationResponse.ID_IS_SAME_AS_CURRENT.getDescription());
                return "redirect:/user/home";
            case 3:
                redirectAttributes.addFlashAttribute("information", PaymentValidationResponse.USER_BANNED.getDescription());
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

    private int checkBeforePayment(Integer senderId, Integer recipientId, Double amount) {
        if(PaymentService.checkIfUserNull(senderId, recipientId, userService)) return 0;
        if(PaymentService.checkIfUserHasEnoughMoney(amount, userService.getUserDetailsByUserId(senderId))) return 1;
        if(PaymentService.checkIfUserTheSameAsCurrent(senderId, recipientId)) return 2;
        if(PaymentService.checkIfUserIsBanned(recipientId, userService)) return 3;
        return 10; // КРИНЖ!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
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
