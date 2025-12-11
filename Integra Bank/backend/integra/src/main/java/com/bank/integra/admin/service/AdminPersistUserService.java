package com.bank.integra.admin.service;

import com.bank.integra.general.repository.RolesRepository;
import com.bank.integra.user.repository.UserDetailsRepository;
import com.bank.integra.user.model.UserDetails;
import com.bank.integra.user.model.User;
import com.bank.integra.general.model.Role;
import com.bank.integra.admin.dto.AdminDTO;
import com.bank.integra.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;

@Service
@Slf4j
public class AdminPersistUserService {
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final UserDetailsRepository userDetailsRepository;
    private final RolesRepository rolesRepository;

    public AdminPersistUserService(PasswordEncoder passwordEncoder, UserService userService,
                                   UserDetailsRepository userDetailsRepository, RolesRepository rolesRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
        this.userDetailsRepository = userDetailsRepository;
        this.rolesRepository = rolesRepository;
    }


    @Transactional
    public void saveUserFromForm(AdminDTO adminDTO, Model model) {
        String hashedPassword = passwordEncoder.encode(adminDTO.getPassword());
        String logMessage = "User " + adminDTO.getUserId() + " saved.";
        log.info(logMessage);
        User user = new User(adminDTO.getUserId(), hashedPassword, true);
        log.info(user.getDtype());
        UserDetails userDetails = new UserDetails(adminDTO.getUserId(), adminDTO.getBalance(), adminDTO.getFirstName(), adminDTO.getLastName(), "", adminDTO.getEmail());
        user.setUserDetails(userDetails);
        Role role = new Role(adminDTO.getUserId(), "EMPLOYEE");

        userService.createUser(user, userDetails);
        rolesRepository.save(role);
        model.addAttribute("successSave", "User " + adminDTO.getUserId() + " has been successfully created.");
    }
}
