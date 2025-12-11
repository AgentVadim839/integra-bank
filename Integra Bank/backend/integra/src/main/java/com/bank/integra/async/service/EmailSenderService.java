package com.bank.integra.async.service;

import com.bank.integra.security.token.service.PasswordResetTokenService;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailSenderService {
    private final JavaMailSender mailSender;
    private final PasswordResetTokenService resetTokenService;
    private final String baseUrl;

    public EmailSenderService(JavaMailSender mailSender, PasswordResetTokenService resetTokenService,
                              @Value("${app.base-url}")String apiUrl) {
        this.mailSender = mailSender;
        this.resetTokenService = resetTokenService;
        this.baseUrl = apiUrl;
    }



    //TODO Make universal sending template, for example, enum with passwords, bans, logins, etc. (PaymentValidationResponse for reference)
    /**
     *
     * @Async - метод работает асинхронно. Достаточно просто запроса с контроллера, как задача отправляется в пул и исоплнится, без лишних блоков.
     * Как вариант, можно попробовать очередь или планировщик. (Но там будет новая сущность и с бд + @Scheduled)
     */
    @Async
    public void sendPasswordResetConfirmation(String toEmail) {
        try {
            String resetLink = generateResetLink(toEmail);
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(message, true, "UTF-8");

            messageHelper.setTo(toEmail);
            messageHelper.setSubject("Password change confirmation.");
            messageHelper.setText(buildEmailBody(resetLink), true);

            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Не удалось отправить письмо: " + e.getMessage());
        }
    }

    private String generateResetLink(String email) {
        String token = resetTokenService.createResetTokenForUser(email.trim());
        String resetLink = baseUrl + "/user/reset-password?token=" + token;
        return resetLink;
    }

    private String buildEmailBody(String resetLink) {
        return """
            <h2>Password change</h2>
            <p>Somebody requested the change of your password.</p>
            <p>The link will be active for the next 15 minutes.</p>
            <p>To confirm, please click the button below:</p>
            <a href="%s" style="display:inline-block;background-color:#4CAF50;color:white;padding:10px 20px;text-decoration:none;border-radius:5px;">Confirm change</a>
            <p>If you didn't request the change, please ignore the letter.</p>
        """.formatted(resetLink);
    }

}
