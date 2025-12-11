package com.bank.integra.transaction.service;

import com.bank.integra.user.repository.UserDetailsRepository;
import com.bank.integra.user.model.UserDetails;
import com.bank.integra.async.AsyncManager;
import com.bank.integra.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@Slf4j
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
    @Retryable(
            retryFor = { ObjectOptimisticLockingFailureException.class },
            maxAttempts = 3,
            backoff = @Backoff(delay = 50)
    )
    public void makePayment(Integer payerPersonId, Integer receiverPersonId, BigDecimal amount, UUID idempotencyKey) {
        if (checkIfUserTheSameAsCurrent(payerPersonId, receiverPersonId)) return;
        if (checkIfUserNull(payerPersonId, receiverPersonId, userService)) return;
        if (checkIfUserIsBanned(receiverPersonId, userService)) return;
        if(!transactionsService.existsByIdempotencyKey(idempotencyKey.toString())) {
            UserDetails payerUserDetails = userService.getUserDetailsByUserId(payerPersonId);
            UserDetails receiverUserDetails = userService.getUserDetailsByUserId(receiverPersonId);
            if (checkIfUserHasEnoughMoney(amount, payerUserDetails)) {
                return;
            }
            payerUserDetails.setBalance(payerUserDetails.getBalance().subtract(amount));
            receiverUserDetails.setBalance(receiverUserDetails.getBalance().add(amount));
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
            log.error("Payment operation wasn't successful.");
        }
    }

    public static boolean checkIfUserHasEnoughMoney(BigDecimal amount, UserDetails payerUserDetails) {
        if (payerUserDetails.getBalance().compareTo(amount) < 0) {
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
            new BigDecimal(amount);
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
