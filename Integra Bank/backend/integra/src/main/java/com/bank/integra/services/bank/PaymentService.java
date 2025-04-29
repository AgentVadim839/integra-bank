package com.bank.integra.services.bank;

import com.bank.integra.dao.UserDetailsRepository;
import com.bank.integra.entities.details.UserDetails;
import com.bank.integra.entities.person.AbstractPerson;
import com.bank.integra.entities.person.User;
import com.bank.integra.services.person.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

//TODO Эта херня должна ещё генерировать транзекшн хистори и квитанцию пдф и валидации нада дахуа
@Service
public class PaymentService {
    @Autowired
    private UserService userService;

    @Autowired
    private UserDetailsRepository userDetailsRepository;

    public PaymentService() {}

    public void makePayment(Integer payerPersonId, Integer receiverPersonId, Double amount, Model model) {
        UserDetails payerUserDetails = userService.getUserDetailsByUserId(payerPersonId);
        UserDetails receiverUserDetails = userService.getUserDetailsByUserId(receiverPersonId);
        if (payerUserDetails.getBalance() < amount) {
            model.addAttribute("paymentError", "Not enough funds for transfer operation.");
        }
        payerUserDetails.setBalance(payerUserDetails.getBalance() - amount);
        receiverUserDetails.setBalance(receiverUserDetails.getBalance() + amount);
        userDetailsRepository.save(payerUserDetails);
        userDetailsRepository.save(receiverUserDetails);
    }
}
