package com.bank.integra.services.bank;

import com.bank.integra.dao.TransactionsRepository;
import com.bank.integra.dao.UserDetailsRepository;
import com.bank.integra.entities.details.UserDetails;
import com.bank.integra.services.person.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.LocalDateTime;
import java.util.UUID;

//TODO Каждый sout - громкий пук, который отдаляет от логгера, не меняем!!
//TODO валидации нада дахуа (себе можно отправить) + проверка на бан пользователя, забаненым не шлём бабло.
@Service
public class PaymentService {
    @Autowired
    private UserService userService;

    @Autowired
    private UserDetailsRepository userDetailsRepository;

    @Autowired
    private TransactionsService transactionsService;

    @Autowired
    private AsyncPdfGenerationService pdfGenerationService;

    //TODO убери, тут не срут
    @Autowired
    private TransactionsRepository transactionsRepository;

    public PaymentService() {}

    @Transactional
    public void makePayment(Integer payerPersonId, Integer receiverPersonId, Double amount, UUID idempotencyKey) {
        if (checkIfUserNull(payerPersonId, receiverPersonId, userService)) return;
        if(!transactionsRepository.existsByIdempotencyKey(idempotencyKey.toString())) {
            UserDetails payerUserDetails = userService.getUserDetailsByUserId(payerPersonId);
            UserDetails receiverUserDetails = userService.getUserDetailsByUserId(receiverPersonId);
            if (checkIfUserHasEnoughMoney(amount, payerUserDetails)) {
                return;
            }
            payerUserDetails.setBalance(payerUserDetails.getBalance() - amount);
            receiverUserDetails.setBalance(receiverUserDetails.getBalance() + amount);
            userDetailsRepository.save(payerUserDetails);
            userDetailsRepository.save(receiverUserDetails);
            if(transactionsService.createAndSave(payerPersonId, receiverPersonId, amount, "", idempotencyKey) == null) {
                throw new IllegalArgumentException();
            }
            int transactionId = transactionsService.findTransactionIdByIdempotencyKey(idempotencyKey.toString());
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    // Этот код запустится после успешного коммита
                    pdfGenerationService.generateReceiptAsync(transactionId+"");
                }
            });
        } else {
            System.out.println("womp womp");
        }
    }

    public static boolean checkIfUserHasEnoughMoney(Double amount, UserDetails payerUserDetails) {
        if (payerUserDetails.getBalance() < amount) {
            return true;
        }
        return false;
    }

    public static boolean checkIfUserNull(Integer payerPersonId, Integer receiverPersonId, UserService userService) {
        if (userService.getUserDetailsByUserId(receiverPersonId) == null || userService.getUserDetailsByUserId(payerPersonId) == null) {
            return true;
        }
        return false;
    }
}
