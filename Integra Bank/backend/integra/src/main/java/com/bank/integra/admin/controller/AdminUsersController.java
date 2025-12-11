package com.bank.integra.admin.controller;

import com.bank.integra.user.model.User;
import com.bank.integra.general.enums.EmailValidationResponse;
import com.bank.integra.admin.dto.AdminDTO;
import com.bank.integra.admin.service.AdminUpdateUserService;
import com.bank.integra.user.service.UserService;
import com.bank.integra.general.validation.EmailValidator;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

//TODO Make more responsive and accessible info-dash like user's transactions one.
@Controller
@RequestMapping("/admin/users")
@Slf4j
public class AdminUsersController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final AdminUpdateUserService adminUpdateUserService;
    private final EmailValidator emailValidator;

    public AdminUsersController(UserService userService, PasswordEncoder passwordEncoder,
                                AdminUpdateUserService adminUpdateUserService, EmailValidator emailValidator) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.adminUpdateUserService = adminUpdateUserService;
        this.emailValidator = emailValidator;
    }

    @GetMapping("")
    public String showAllUsers(Model model) {
        List<User> users = userService.getAllUsers();
        String logMessage = "Count of users app got from service is: " + (users != null ? users.size() : "null");
        log.info(logMessage);
        if (users != null) {
            for (User user : users) {
                String logMessageId = "User ID: " + user.getId() + ", First Name: " + (user.getUserDetails() != null ? user.getUserDetails().getFirstName() : "No Details");
                log.info(logMessageId);
            }
        }
        model.addAttribute("users", users);
        return "adminUsersList";
    }

    @PostMapping("/ban/{id}")
    public String banUser(@PathVariable int id) {
        User user = userService.getUserById(id);
        if (user == null) {
            throw new RuntimeException("User " + id + " not found.");
        }

        user.setActive(false);
        userService.updateUser(user);
        return "redirect:/admin/users?banned=true";
    }

    @PostMapping("/unban/{id}")
    public String unbanUser(@PathVariable int id) {
        User user = userService.getUserById(id);
        if (user == null) {
            throw new RuntimeException("User " + id + " not found.");
        }

        user.setActive(true);
        userService.updateUser(user);
        return "redirect:/admin/users?banned=true";
    }

    // Aint no model attributes required, js already sends success message after deleting.
    @PostMapping("/delete/{id}")
    public String deleteUser(@PathVariable int id) {
        User user = userService.getUserById(id);
         if(user == null) {
             throw new RuntimeException("User " + id + " not found.");
         }

         userService.deleteUserById(id);
         return "redirect:/admin/users?deleted=true";
    }

    @GetMapping("/edit/{id}")
    public String editUser(@PathVariable int id, Model model) {
        User user = userService.getUserById(id);
        if(user == null) {
            return "redirect:/admin/users";
        }

        AdminDTO adminDTO = new AdminDTO();
        adminDTO.setUserId(user.getId());
        adminDTO.setFirstName(user.getUserDetails().getFirstName());
        adminDTO.setLastName(user.getUserDetails().getLastName());
        adminDTO.setEmail(user.getUserDetails().getEmail());
        model.addAttribute("editUser", adminDTO);
        return "adminEditUser";
    }


    @PostMapping("/update/{id}")
    public String updateUser(@PathVariable Integer id, @ModelAttribute @Valid AdminDTO adminDTO, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if(bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Validation failed");
            return "redirect:/admin/users";
        }
        EmailValidationResponse response = emailValidator.checkEmail(adminDTO.getEmail(), adminDTO.getUserId());
        if(response.isSuccess() || response.getDescription().equals(EmailValidationResponse.EMAIL_IS_SAME_AS_CURRENT.getDescription())) {
            adminUpdateUserService.updateUserFromForm(id, adminDTO, bindingResult, redirectAttributes);
            redirectAttributes.addFlashAttribute("information", "User was edited successfully.");
        } else {
            redirectAttributes.addFlashAttribute("information", response.getDescription());
        }
        return "redirect:/admin/users";
    }
}
