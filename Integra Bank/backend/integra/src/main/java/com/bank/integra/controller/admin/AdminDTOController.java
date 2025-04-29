package com.bank.integra.controller.admin;


import com.bank.integra.dao.RolesRepository;
import com.bank.integra.dao.UserDetailsRepository;
import com.bank.integra.dao.UserRepository;
import com.bank.integra.entities.details.UserDetails;
import com.bank.integra.entities.person.User;
import com.bank.integra.entities.role.Role;
import com.bank.integra.services.DTO.AdminDTO;
import com.bank.integra.services.person.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminDTOController {
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserService userService;
    @Autowired
    UserDetailsRepository userDetailsRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RolesRepository rolesRepository;

    //TODO хуйня, валидацию тоже надо

    @PostMapping("/save-user")
    public String processForm(@Valid @ModelAttribute AdminDTO adminDTO, BindingResult bindingResult, Model model) {
        if(bindingResult.hasErrors()) {
            return "/admin/home";
        }
        String hashedPassword = passwordEncoder.encode(adminDTO.getPassword());
        System.out.println("User " + adminDTO.getUserId() + " saved.");
        User user = new User(adminDTO.getUserId(), hashedPassword, true);
        System.out.println(user.getDtype());
        UserDetails userDetails = new UserDetails(adminDTO.getUserId(), adminDTO.getBalance(), adminDTO.getFirstName(), adminDTO.getLastName(), "", adminDTO.getEmail());
        user.setUserDetails(userDetails);
        Role role = new Role(adminDTO.getUserId(), "EMPLOYEE");

        userService.createUser(user, userDetails);
        rolesRepository.save(role);
        model.addAttribute("User " + adminDTO.getUserId() + " has been successfully created.");
        return "result";
    }
}
