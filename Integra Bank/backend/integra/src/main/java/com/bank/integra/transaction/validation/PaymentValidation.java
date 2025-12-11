package com.bank.integra.transaction.validation;

import com.bank.integra.general.enums.PaymentValidationResponse;
import com.bank.integra.user.service.UserService;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class PaymentValidation {
    private final UserService userService;

    public PaymentValidation(UserService userService) {
        this.userService = userService;
    }

    public PaymentValidationResponseDTO validatePayment(Integer senderId, Integer recipientId, BigDecimal amount) {
        // Проверка на нуллы
        if (userService.getUserDetailsByUserId(recipientId) == null || userService.getUserDetailsByUserId(senderId) == null) {
            return new PaymentValidationResponseDTO(PaymentValidationResponse.INVALID_FORMAT.getDescription());
        }

        // Проверка на баланс
        if (userService.getUserDetailsByUserId(senderId).getBalance().compareTo(amount) < 0) {
            return new PaymentValidationResponseDTO(PaymentValidationResponse.NOT_ENOUGH_FUNDS.getDescription());
        }

        // Проверка на перевод самому себе (аналог checkIfUserTheSameAsCurrent)
        if (senderId.equals(recipientId)) {
            return new PaymentValidationResponseDTO(PaymentValidationResponse.ID_IS_SAME_AS_CURRENT.getDescription());
        }

        // Проверка на бан (аналог checkIfUserIsBanned)
        if (!userService.getUserById(recipientId).isActive()) {
            return new PaymentValidationResponseDTO(PaymentValidationResponse.USER_BANNED.getDescription());
        }

        return new PaymentValidationResponseDTO(PaymentValidationResponse.OK.getDescription(), true);
    }
}
