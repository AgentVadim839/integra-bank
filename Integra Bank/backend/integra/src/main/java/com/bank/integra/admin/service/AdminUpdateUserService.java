package com.bank.integra.admin.service;

import com.bank.integra.user.model.UserDetails;
import com.bank.integra.user.model.User;
import com.bank.integra.admin.dto.AdminDTO;
import com.bank.integra.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Service

public class AdminUpdateUserService {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public AdminUpdateUserService(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void updateUserFromForm(Integer id, AdminDTO adminDTO, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        User user = userService.getUserById(adminDTO.getUserId());
        if(!adminDTO.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(adminDTO.getPassword()));
        }
        UserDetails userDetails = userService.getUserDetailsByUserId(adminDTO.getUserId());
        if(adminDTO.getBalance() != null) {
            userDetails.setBalance(adminDTO.getBalance());
        }
        userDetails.setFirstName(adminDTO.getFirstName());
        userDetails.setLastName(adminDTO.getLastName());
        userDetails.setEmail(adminDTO.getEmail());
        user.setUserDetails(userDetails);
        if (user == null) {
            throw new RuntimeException("User " + id + " not found.");
        }
        userService.updateUser(user);
        redirectAttributes.addFlashAttribute("update", true);
    }
}
