package com.bank.integra.user.service;

import com.bank.integra.admin.service.AdminUserDetailsService;
import com.bank.integra.user.model.User;
import com.bank.integra.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.stream.Collectors;

//TODO Нужен логгер для логов

/**
 * Этот класс распознаёт юзеров по их ролям. То есть если роль юзера: "ROLE_EMPLOYEE", тогда пускаем, иначе вам на другой логин. (Так же с {@link AdminUserDetailsService})
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {
    //TODO А че не сервис?
    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException, DisabledException {
        try {
            Integer id = Integer.parseInt(userId);
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + userId));
            if(!user.isActive()) {
                throw new DisabledException("User account is disabled.");
            }
            return createUserDetails(user.getId(), user.getPassword(), user.getRoles());
        } catch (NumberFormatException f) {
            throw new UsernameNotFoundException("Invalid user id: " + userId);
        }
    }

    private UserDetails createUserDetails(Integer id, String password, List<String> roles) {
        List<GrantedAuthority> authorities = roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toList());
        if (!authorities.contains(new SimpleGrantedAuthority("ROLE_EMPLOYEE"))) {
            throw new UsernameNotFoundException("User with id " + id + " is not an EMPLOYEE");
        }
        System.out.println("User: {" + id + "} has roles: {" + authorities + "}");
        return new org.springframework.security.core.userdetails.User(
                String.valueOf(id),
                password,
                authorities
        );
    }


}