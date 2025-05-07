package com.bank.integra.services.bank;

import com.bank.integra.dao.TransactionsRepository;
import com.bank.integra.dao.UserDetailsRepository;
import com.bank.integra.entities.details.UserDetails;
import com.bank.integra.entities.person.AbstractPerson;
import com.bank.integra.entities.person.User;
import com.bank.integra.services.person.TransactionsService;
import com.bank.integra.services.person.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;

import java.util.UUID;

//TODO Каждый sout - громкий пук, который отдаляет от логгера, не меняем!!
//TODO квитанцию пдф и валидации нада дахуа
@Service
public class PaymentService {
    @Autowired
    private UserService userService;

    @Autowired
    private UserDetailsRepository userDetailsRepository;

    @Autowired
    private TransactionsService transactionsService;

    //TODO убери, тут не срут
    @Autowired
    private TransactionsRepository transactionsRepository;

    public PaymentService() {}

    @Transactional
    public void makePayment(Integer payerPersonId, Integer receiverPersonId, Double amount, UUID idempotencyKey, Model model) {
        if(!transactionsRepository.existsByIdempotencyKey(idempotencyKey.toString())) {
            if (receiverPersonId == payerPersonId || userService.getUserDetailsByUserId(receiverPersonId) == null || userService.getUserDetailsByUserId(payerPersonId) == null) {
                model.addAttribute("paymentErrorInvalidPayerId", "The user id is invalid. Please, try again.");
                return;
            }
            UserDetails payerUserDetails = userService.getUserDetailsByUserId(payerPersonId);
            UserDetails receiverUserDetails = userService.getUserDetailsByUserId(receiverPersonId);
            if (payerUserDetails.getBalance() < amount) {
                model.addAttribute("paymentErrorNotEnoughFunds", "Not enough funds for transfer operation.");
                return;
            }
            payerUserDetails.setBalance(payerUserDetails.getBalance() - amount);
            receiverUserDetails.setBalance(receiverUserDetails.getBalance() + amount);
            userDetailsRepository.save(payerUserDetails);
            userDetailsRepository.save(receiverUserDetails);

            try {
                transactionsService.createAndSave(payerPersonId, receiverPersonId, amount, "", idempotencyKey);
            } catch(DataIntegrityViolationException e) {
                System.out.println("ультра вомп вомп, мы уже внатури сохраняли.");
            }
        } else {
            System.out.println("womp womp");
        }
    }
}
