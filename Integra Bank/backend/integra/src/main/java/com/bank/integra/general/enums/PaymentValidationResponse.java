package com.bank.integra.general.enums;

public enum PaymentValidationResponse {
    /**
     * Валидация прошла успешно.
     */
    OK("Payment OK.", false), // <-- Текст и флаг успешности

    /**
     * Неправильный формат айди. (Например, такого айди не существует)
     */
    INVALID_FORMAT("Given data format is invalid. Please, try again.", true),

    /**
     * Недостаточно средств.
     */
    NOT_ENOUGH_FUNDS("Not enough funds for transfer operation.", true),

    /**
     * Id идентичный.
     */
    ID_IS_SAME_AS_CURRENT("Given id is same as yours.", true),

    /**
     * Пользователь заблокирован.
     */
    USER_BANNED("The user is blocked.", true),

    /**
     * Общая ошибка валидации, не попадающая под другие категории.
     */
    UNKNOWN_ERROR("Unknown error occurred during email validation.", true);


    // Поле для текстового описания
    private final String description;
    // Флаг, указывающий, является ли этот статус ошибкой
    private boolean isError;

    // Конструктор enum (всегда private)
    private PaymentValidationResponse(String description, boolean isError) {
        this.description = description;
        this.isError = isError;
    }


    // Геттеры для полей
    public String getDescription() {
        return description;
    }

    public boolean isError() {
        return isError;
    }

    public void setError(boolean error) {
        isError = error;
    }

    // Дополнительный метод для удобства, если нужна проверка на успех
    public boolean isSuccess() {
        return !isError;
    }


}
