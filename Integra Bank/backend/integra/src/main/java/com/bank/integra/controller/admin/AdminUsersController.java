package com.bank.integra.controller.admin;

import com.bank.integra.entities.person.AbstractPerson;
import com.bank.integra.entities.person.User;
import com.bank.integra.services.person.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin/users")
public class AdminUsersController {
    //TODO Слегка рыгань
    @Autowired
    private UserService userService;

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

    @PostMapping("/delete/{id}")
    public String deleteUser(@PathVariable int id, Model model) {
        User user = userService.getUserById(id);
         if(user == null) {
             throw new RuntimeException("User " + id + " not found.");
         }

         userService.deleteUserById(id);
        return "redirect:/admin/users?deleted=true";
    }
}
