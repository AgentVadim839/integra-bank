package com.bank.integra.security.auth;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {
    private static final String DISABLED_USER_REDIRECT_URL = "/user/login?banned";

    private static final String DEFAULT_FAILURE_URL = "/user/login?error";

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        if (exception instanceof InternalAuthenticationServiceException &&
                exception.getCause() instanceof DisabledException) {
            // Если это DisabledException (бан), перенаправляем на специальный URL
            getRedirectStrategy().sendRedirect(request,response, DISABLED_USER_REDIRECT_URL);
        } else {
            // Для всех других ошибок аутентификации используем стандартное поведение (или другой URL)
            super.setDefaultFailureUrl(DEFAULT_FAILURE_URL);
            super.onAuthenticationFailure(request,response,exception);
        }
    }
}
