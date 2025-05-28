package com.bank.integra.controller.user;

import com.bank.integra.services.DTO.TransferDTO;
import com.bank.integra.services.bank.PaymentService;
import com.bank.integra.services.bank.TransactionsService;
import com.bank.integra.services.person.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
                               @RequestParam Double amount, Model model, RedirectAttributes redirectAttributes) {
        switch(checkBeforePayment(senderId,recipientId,amount)){
            case 0:
                redirectAttributes.addFlashAttribute("information", "The user id is invalid. Please, try again.");
                return "redirect:/user/home";
            case 1:
                redirectAttributes.addFlashAttribute("information", "Not enough funds for transfer operation.");
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

    private int checkBeforePayment(Integer senderId, Integer recipientId, Double amount) {
        if(paymentService.checkIfUserNull(senderId, recipientId, userService)) return 0;
        if(paymentService.checkIfUserHasEnoughMoney(amount, userService.getUserDetailsByUserId(senderId))) return 1;
        return 2;
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
