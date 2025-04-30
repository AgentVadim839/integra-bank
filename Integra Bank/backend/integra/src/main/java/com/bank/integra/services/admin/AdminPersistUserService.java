package com.bank.integra.services.admin;

import com.bank.integra.dao.RolesRepository;
import com.bank.integra.dao.UserDetailsRepository;
import com.bank.integra.entities.details.UserDetails;
import com.bank.integra.entities.person.User;
import com.bank.integra.entities.role.Role;
import com.bank.integra.services.DTO.AdminDTO;
import com.bank.integra.services.person.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;

@Service
public class AdminPersistUserService {
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserService userService;
    @Autowired
    private UserDetailsRepository userDetailsRepository;
    @Autowired
    private RolesRepository rolesRepository;


    @Transactional
    public void saveUserFromForm(AdminDTO adminDTO, Model model) {
        String hashedPassword = passwordEncoder.encode(adminDTO.getPassword());
        System.out.println("User " + adminDTO.getUserId() + " saved.");
        User user = new User(adminDTO.getUserId(), hashedPassword, true);
        System.out.println(user.getDtype());
        UserDetails userDetails = new UserDetails(adminDTO.getUserId(), adminDTO.getBalance(), adminDTO.getFirstName(), adminDTO.getLastName(), "", adminDTO.getEmail());
        user.setUserDetails(userDetails);
        Role role = new Role(adminDTO.getUserId(), "EMPLOYEE");

        userService.createUser(user, userDetails);
        rolesRepository.save(role);
        model.addAttribute("successSave", "User " + adminDTO.getUserId() + " has been successfully created.");
    }
}
