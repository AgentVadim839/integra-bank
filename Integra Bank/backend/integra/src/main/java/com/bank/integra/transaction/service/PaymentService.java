package com.bank.integra.transaction.service;

import com.bank.integra.user.repository.UserDetailsRepository;
import com.bank.integra.user.model.UserDetails;
import com.bank.integra.async.AsyncManager;
import com.bank.integra.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.UUID;

//TODO Каждый sout - громкий пук, который отдаляет от логгера, не меняем!!
//TODO Перевести всю логику валидации в отдельный класс (для контроллера тоже) и слать енумы в виде ответа, как у емаил валидатор
@Service
public class PaymentService {
    private final UserService userService;
    private final UserDetailsRepository userDetailsRepository;
    private final TransactionsService transactionsService;
    private final AsyncManager asyncManager;

    public PaymentService(UserService userService, UserDetailsRepository userDetailsRepository, TransactionsService transactionsService, AsyncManager asyncManager) {
        this.userService = userService;
        this.userDetailsRepository = userDetailsRepository;
        this.transactionsService = transactionsService;
        this.asyncManager = asyncManager;
    }

    @Transactional
    public void makePayment(Integer payerPersonId, Integer receiverPersonId, Double amount, UUID idempotencyKey) {
        if (checkIfUserTheSameAsCurrent(payerPersonId, receiverPersonId)) return;
        if (checkIfUserNull(payerPersonId, receiverPersonId, userService)) return;
        if (checkIfUserIsBanned(receiverPersonId, userService)) return;
        if(!transactionsService.existsByIdempotencyKey(idempotencyKey.toString())) {
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
                    asyncManager.runPdfGenerationTask(transactionId+"");
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

    public static boolean checkIfFormatCorrect(String receiverPersonId, String amount) {
        try {
            Integer.parseInt(receiverPersonId);
            Double.parseDouble(amount);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static boolean checkIfUserIsBanned(Integer recipientId, UserService userService) {
        if(userService.getUserById(recipientId).isActive()) {
            return false;
        } else {
            return true;
        }
    }

    public static boolean checkIfUserTheSameAsCurrent(Integer payerPersonId, Integer receiverPersonId) {
        if(payerPersonId.equals(receiverPersonId)) {
            return true;
        }
        return false;
    }
}
