package com.bank.integra.config;

import com.bank.integra.security.filter.UserStatusInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// Временно офф
public class WebConfig implements WebMvcConfigurer {
    private final UserStatusInterceptor userStatusInterceptor;

    public WebConfig(UserStatusInterceptor userStatusInterceptor) {
        this.userStatusInterceptor = userStatusInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(userStatusInterceptor)
                .addPathPatterns("/user/**")
                .excludePathPatterns("/user/login", "/user/authenticateTheUser", "/user/logout");
    }
}
