package com.bank.integra.security;

import com.bank.integra.security.auth.CustomAuthenticationFailureHandler;
import com.bank.integra.admin.service.AdminUserDetailsService;
import com.bank.integra.user.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

/**
 * Main security configuration class.
 * Defines separate security filter chains for admin and regular users.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    private final CustomUserDetailsService userDetailsService;
    private final AdminUserDetailsService adminUserDetailsService;
    private final CustomAuthenticationFailureHandler customAuthenticationFailureHandler;

    public SecurityConfiguration(CustomUserDetailsService userDetailsService, AdminUserDetailsService adminUserDetailsService, CustomAuthenticationFailureHandler customAuthenticationFailureHandler) {
        this.userDetailsService = userDetailsService;
        this.adminUserDetailsService = adminUserDetailsService;
        this.customAuthenticationFailureHandler = customAuthenticationFailureHandler;
    }

    /**
     * Security configuration for admin routes.
     * Only applies to URLs starting with /admin/
     */
    @Bean
    public SecurityFilterChain adminFilterChain(HttpSecurity http, HandlerMappingIntrospector introspector) throws Exception {
        MvcRequestMatcher adminLoginMatcher = new MvcRequestMatcher(introspector, "/admin/login");
        MvcRequestMatcher adminLoginProcessingMatcher = new MvcRequestMatcher(introspector, "/admin/authenticateTheAdmin");
        adminLoginProcessingMatcher.setServletPath("/");
        http
                .securityMatcher("/admin/**")
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(new MvcRequestMatcher(introspector, "/admin/login")).permitAll()
                        .anyRequest().hasRole("ADMIN")
                )
                .formLogin(form -> form
                        .loginPage("/admin/login")
                        .loginProcessingUrl("/admin/authenticateTheAdmin")
                        .defaultSuccessUrl("/admin/home", true)
                        .failureUrl("/admin/login?error")
                        .usernameParameter("id")
                        .passwordParameter("password")
                )
                .logout(logout -> logout
                        .logoutRequestMatcher(new MvcRequestMatcher(introspector, "/admin/logout"))
                        .logoutSuccessUrl("/admin/login?logout")
                        .permitAll()
                )
                .userDetailsService(adminUserDetailsService);
        return http.build();
    }

    /**
     * Security configuration for user routes.
     * Only applies to URLs starting with /user/
     */
    @Bean
    public SecurityFilterChain userFilterChain(HttpSecurity http, HandlerMappingIntrospector introspector) throws Exception {
        MvcRequestMatcher userLoginMatcher = new MvcRequestMatcher(introspector, "/user/login");
        MvcRequestMatcher userLoginProcessingMatcher = new MvcRequestMatcher(introspector, "/user/authenticateTheUser");
        userLoginProcessingMatcher.setServletPath("/");
        MvcRequestMatcher userLogoutMatcher = new MvcRequestMatcher(introspector, "/user/logout");

        http
                .securityMatcher("/user/**")
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(userLoginMatcher).permitAll()
                        .anyRequest().hasRole("EMPLOYEE")
                )
                .formLogin(form -> form
                        .loginPage("/user/login")
                        .loginProcessingUrl("/user/authenticateTheUser")
                        .defaultSuccessUrl("/user/home", true)
                        .failureHandler(customAuthenticationFailureHandler)
                        .usernameParameter("id")
                        .passwordParameter("password")
                )
                .logout(logout -> logout
                        .logoutRequestMatcher(userLogoutMatcher)
                        .logoutSuccessUrl("/user/login?logout")
                        .permitAll()
                )
                .userDetailsService(userDetailsService);
        return http.build();
    }

}