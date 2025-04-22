package com.bank.integra.controller.admin;

import com.bank.integra.entities.person.Admin;
import com.bank.integra.entities.person.User;
import com.bank.integra.services.person.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminDashController {

    @GetMapping("/")
    public String redirectToMain() {
        return "redirect:/admin/home";
    }

    @GetMapping("/home")
    public String showMainAdminPage() {
        return "adminDash";
    }
}
