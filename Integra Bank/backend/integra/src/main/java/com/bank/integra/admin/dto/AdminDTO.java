package com.bank.integra.admin.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AdminDTO {
    @NotNull(message = "User ID cannot be null")
    @Positive(message = "User ID must be positive")
    private Integer userId;

    @NotBlank(message = "Password cannot be blank")
    @Size(min = 8, max = 255, message = "Password must be between 8 and 255 characters")
    private String password;

    @NotNull(message = "Balance cannot be null")
    @DecimalMin(value = "0.0", message = "Balance must be at least 0.0")
    private BigDecimal balance;

    @NotBlank(message = "First name cannot be blank")
    @Size(max = 255, message = "First name must be less than 255 characters")
    private String firstName;

    @NotBlank(message = "Last name cannot be blank")
    @Size(max = 255, message = "Last name must be less than 255 characters")
    private String lastName;

    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Email must be a valid email address")
    @Size(max = 255, message = "Email must be less than 255 characters")
    private String email;
}
