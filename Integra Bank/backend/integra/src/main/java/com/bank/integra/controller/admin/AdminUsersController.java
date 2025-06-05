package com.bank.integra.controller.admin;

import com.bank.integra.dao.UserDetailsRepository;
import com.bank.integra.entities.details.UserDetails;
import com.bank.integra.entities.person.User;
import com.bank.integra.services.DTO.AdminDTO;
import com.bank.integra.services.admin.AdminUpdateUserService;
import com.bank.integra.services.person.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

//TODO Сделать норм инфо-поле, как у юзера с транзакциями.
@Controller
@RequestMapping("/admin/users")
public class AdminUsersController {
    //TODO Слегка рыгань
    @Autowired
    private UserService userService;


    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AdminUpdateUserService adminUpdateUserService;

    @GetMapping("")
    public String showAllUsers(Model model) {
        List<User> users = userService.getAllUsers();
        System.out.println("Количество пользователей, полученных из сервиса: " + (users != null ? users.size() : "null"));
        if (users != null) {
            for (User user : users) {
                System.out.println("User ID: " + user.getId() + ", First Name: " + (user.getUserDetails() != null ? user.getUserDetails().getFirstName() : "No Details"));
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
        adminUpdateUserService.updateUserFromForm(id, adminDTO, bindingResult, redirectAttributes);
        return "redirect:/admin/users";
    }
}
