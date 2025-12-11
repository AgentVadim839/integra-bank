package com.bank.integra.user.controller;

import com.bank.integra.user.model.User;
import com.bank.integra.security.token.model.PasswordResetToken;
import com.bank.integra.user.service.UserService;
import com.bank.integra.security.token.service.PasswordResetTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;

//TODO Password validation
//TODO Try to make async password sending operation (Scheduled)
//TODO Register sessions, so after password change all sessions will be automatically kicked.
@RequestMapping("/user")
@Controller
public class PasswordResetController {
    private final PasswordResetTokenService resetTokenService;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public PasswordResetController(PasswordResetTokenService resetTokenService, UserService userService, PasswordEncoder passwordEncoder) {
        this.resetTokenService = resetTokenService;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/reset-password")
    public String showResetPasswordPage(@RequestParam("token") String token, Model model) {
        PasswordResetToken resetToken = resetTokenService.findByToken(token);

        if(resetToken == null || resetToken.isUsed() || resetToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            model.addAttribute("message", "Token is invalid or expired.");
            return "error-password-reset";
        }

        model.addAttribute("token", token);
        return "reset-password";
    }

    @PostMapping("/reset-password")
    public String handlePasswordReset(@RequestParam("token") String token,
                                      @RequestParam("password") String newPassword,
                                      Model model) {
        PasswordResetToken resetToken = resetTokenService.findByToken(token);

        if (resetToken == null || resetToken.isUsed() || resetToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            model.addAttribute("message", "Token is invalid or expired.");
            return "error-password-reset";
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword)); // зашифровать пароль
        userService.updateUser(user);

        resetToken.setUsed(true);
        resetTokenService.save(resetToken);

        model.addAttribute("message", "Password was changed successfully.");
        return "success-password-reset"; // страница входа после успешной смены пароля
    }
}
