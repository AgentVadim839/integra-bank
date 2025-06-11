package com.bank.integra.services.validation;


import com.bank.integra.enums.EmailValidationResponse;
import com.bank.integra.services.person.UserService;
import org.springframework.beans.factory.annotation.Autowired;

public class EmailValidation {

    public static EmailValidationResponse checkEmail(String email, Integer userId, UserService userService) {
        if(email == null || email.trim().isEmpty()) {
            return EmailValidationResponse.INVALID_FORMAT;
        }

        if(!email.matches("^[\\w-.]+@[\\w-]+\\.[a-zA-Z]{2,}$")) {
            return EmailValidationResponse.INVALID_FORMAT;
        }

        //TODO API проверка на временный эмаил, https://disposable.debounce.io/?email=info@example.com

        if(userService.existsByUserId(userId) && userService.getUserDetailsByUserId(userId).getEmail().equals(email)) {
            return EmailValidationResponse.EMAIL_IS_SAME_AS_CURRENT;
        }

        if(userService.existsByEmail(email)) {
            return EmailValidationResponse.ALREADY_TAKEN;
        }

        if(email.length() > 254) {
            return EmailValidationResponse.TOO_LONG;
        }


        return EmailValidationResponse.OK;
    }

}
