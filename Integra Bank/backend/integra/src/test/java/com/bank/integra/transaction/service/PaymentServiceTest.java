package com.bank.integra.transaction.service;

import com.bank.integra.async.AsyncManager;
import com.bank.integra.transaction.model.Transaction;
import com.bank.integra.user.model.User;
import com.bank.integra.user.model.UserDetails;
import com.bank.integra.user.repository.UserDetailsRepository;
import com.bank.integra.user.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceTest {
    //Успешный перевод.
    //
    //Нехватка денег.
    //
    //Неверный ID.

    @Mock
    UserService userService;
    @Mock
    UserDetailsRepository userDetailsRepository;
    @Mock
    TransactionsService transactionsService;
    @Mock
    AsyncManager asyncManager;
    @InjectMocks
    PaymentService paymentService;

    @Test
    void makePayment_shouldTransferMoney_whenUserIsValidAndHasBalance() {
        Integer payerId = 1;
        Integer receiverId = 2;
        BigDecimal amount = new BigDecimal(100.00);
        UUID idempotencyKey = UUID.randomUUID();

        UserDetails payerDetails = new UserDetails();
        payerDetails.setBalance(new BigDecimal(500));
        payerDetails.setUserId(payerId);

        UserDetails receiverDetails = new UserDetails();
        receiverDetails.setBalance(new BigDecimal(50));
        receiverDetails.setUserId(receiverId);

        User receiverUser = new User();
        receiverUser.setActive(true);

        when(userService.getUserDetailsByUserId(payerId)).thenReturn(payerDetails);
        when(userService.getUserDetailsByUserId(receiverId)).thenReturn(receiverDetails);

        when(userService.getUserById(receiverId)).thenReturn(receiverUser);

        when(transactionsService.existsByIdempotencyKey(idempotencyKey.toString())).thenReturn(false);

        // eq() - это "равно этому значению".
        when(transactionsService.createAndSave(eq(payerId), eq(receiverId), eq(amount), anyString(), eq(idempotencyKey)))
                .thenReturn(new Transaction());


        // Мы создаем "пузырь", внутри которого TransactionSynchronizationManager - это мок
        try (var mockedStatic = Mockito.mockStatic(TransactionSynchronizationManager.class)) {

            // Тут мы говорим: "Слушай, если кто-то вызовет registerSynchronization, ничего не делай, просто кивни"
            // (Для void методов моки по умолчанию ничего не делают, так что можно даже не настраивать when)

            // Запускаем наш метод внутри "пузыря"
            paymentService.makePayment(payerId, receiverId, amount, idempotencyKey);

            // ВАЖНО: Код внутри afterCommit() (генерация PDF) НЕ выполнится,
            // потому что это просто юнит-тест. Мы проверяем только факт регистрации.
        }

        Assertions.assertEquals(new BigDecimal(400.00), payerDetails.getBalance());
        Assertions.assertEquals(new BigDecimal(150.00), receiverDetails.getBalance());

        verify(userDetailsRepository).save(payerDetails);
        verify(userDetailsRepository).save(receiverDetails);
    }

    @Test
    void makePayment_shouldNotTransferMoney_whenUserIsBrokeMob() {
        Integer payerId = 1;
        Integer receiverId = 2;
        BigDecimal amount = new BigDecimal(100.00);
        UUID idempotencyKey = UUID.randomUUID();

        UserDetails payerDetails = new UserDetails();
        payerDetails.setBalance(new BigDecimal(50));
        payerDetails.setUserId(payerId);

        UserDetails receiverDetails = new UserDetails();
        receiverDetails.setBalance(new BigDecimal(50));
        receiverDetails.setUserId(receiverId);

        User receiverUser = new User();
        receiverUser.setActive(true);

        when(userService.getUserDetailsByUserId(payerId)).thenReturn(payerDetails);
        when(userService.getUserDetailsByUserId(receiverId)).thenReturn(receiverDetails);

        when(userService.getUserById(receiverId)).thenReturn(receiverUser);

        when(transactionsService.existsByIdempotencyKey(idempotencyKey.toString())).thenReturn(false);

        paymentService.makePayment(payerId, receiverId, amount, idempotencyKey);

        Assertions.assertEquals(new BigDecimal(50), payerDetails.getBalance());
        Assertions.assertEquals(new BigDecimal(50), receiverDetails.getBalance());
    }

    @Test
    void makePayment_shoudNotTransferMoney_whenUserIsBannedMob() {
        Integer payerId = 1;
        Integer receiverId = 2;
        BigDecimal amount = new BigDecimal(100.00);
        UUID idempotencyKey = UUID.randomUUID();

        UserDetails payerDetails = new UserDetails();
        payerDetails.setBalance(new BigDecimal(500));
        payerDetails.setUserId(payerId);

        UserDetails receiverDetails = new UserDetails();
        receiverDetails.setBalance(new BigDecimal(50));
        receiverDetails.setUserId(receiverId);

        User receiverUser = new User();
        receiverUser.setActive(false);

        when(userService.getUserDetailsByUserId(payerId)).thenReturn(payerDetails);
        when(userService.getUserDetailsByUserId(receiverId)).thenReturn(receiverDetails);

        when(userService.getUserById(receiverId)).thenReturn(receiverUser);

        paymentService.makePayment(payerId, receiverId, amount, idempotencyKey);

        Assertions.assertEquals(new BigDecimal(500), payerDetails.getBalance());
        Assertions.assertEquals(new BigDecimal(50), receiverDetails.getBalance());
    }
}
