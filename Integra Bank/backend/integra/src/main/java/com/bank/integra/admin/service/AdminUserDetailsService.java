package com.bank.integra.admin.service;

import com.bank.integra.admin.repository.AdminRepository;
import com.bank.integra.admin.model.Admin;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AdminUserDetailsService implements UserDetailsService {
    private final AdminRepository adminRepository;

    public AdminUserDetailsService(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String adminId) throws UsernameNotFoundException {
        try {
            Integer id = Integer.parseInt(adminId);
            Admin admin = adminRepository.findById(id)
                    .orElseThrow(() -> new UsernameNotFoundException("Admin not found with id: " + adminId));
            return createUserDetails(admin.getId(), admin.getPassword(), admin.getRoles());
        } catch(NumberFormatException f) {
            throw new UsernameNotFoundException("Invalid admin id: " + adminId);
        }
    }

    private UserDetails createUserDetails(Integer id, String password, List<String> roles) {
        List<GrantedAuthority> authorities = roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toList());
        if (!authorities.contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            throw new UsernameNotFoundException("User with id " + id + " is not an ADMIN");
        }
        String logMessage = "User: {"+id+"} has roles: {"+authorities+"}";
        log.info(logMessage);
        return new org.springframework.security.core.userdetails.User(
                String.valueOf(id),
                password,
                authorities
        );
    }
}
