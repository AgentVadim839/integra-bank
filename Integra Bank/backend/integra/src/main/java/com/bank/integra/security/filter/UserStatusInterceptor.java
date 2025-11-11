package com.bank.integra.security.filter;

import org.springframework.security.core.userdetails.UserDetails;
import com.bank.integra.user.model.User;
import com.bank.integra.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

// Определяет, заблокирован ли юзер/существует ли он в бд каждый запрос
// Чтобы при бане его выкинуло в рантайме. (грузит бд!!!)
@Component
public class UserStatusInterceptor implements HandlerInterceptor {
    private final UserService userService;

    public UserStatusInterceptor(UserService userService) {
        this.userService = userService;
    }

    private final SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // Проверяем, что пользователь аутентифицирован и это не анонимный пользователь
        if(authentication != null && authentication.isAuthenticated()
        && !(authentication.getPrincipal() instanceof String && authentication.getAuthorities().equals("anonymousUser"))) {

            // Получаем UserDetails, чтобы взять userId
            Object principal = authentication.getPrincipal();
            Integer userId = null;

            if(principal instanceof UserDetails) {
                try{
                    // Обработка, если username не является числовым ID
                    // Используем секурити юзер детайлс, тк там данные нужного здесь типа (ID)
                    userId = Integer.parseInt(((UserDetails) principal).getUsername());
                } catch (NumberFormatException e) {
                    System.out.println("Authenticated user ID is not a number: " + ((UserDetails) principal).getUsername());
                    // Можно вылогинить или просто пропустить эту проверку для данного пользователя
                    return true;
                }
            }

            if(userId != null) {
                User user = userService.getUserById(userId);
                // Если пользователь не найден (удален из БД) или не активен
                if(user == null || !user.isActive()) {
                    // Вылогинить пользователя
                    logoutHandler.logout(request, response, authentication);
                    response.sendRedirect(request.getContextPath() + "/user/login?forcedLogout");
                    return false; // Запрос не может продолжаться
                }
            }

        }
        return true; // Запрос может продолжаться
    }
}
