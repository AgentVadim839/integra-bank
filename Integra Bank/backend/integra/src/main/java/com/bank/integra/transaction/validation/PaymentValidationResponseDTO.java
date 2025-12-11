package com.bank.integra.transaction.validation;

public class PaymentValidationResponseDTO {
    private String message;
    private boolean isValid;

    public PaymentValidationResponseDTO(String message) {
        this.message = message;
    }

    public PaymentValidationResponseDTO(String message, boolean isValid) {
        this.message = message;
        this.isValid = isValid;
    }

    public boolean isValid() {
        return isValid;
    }

    public String getMessage() {
        return message;
    }
}
