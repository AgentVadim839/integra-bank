package com.bank.integra.admin.controller;


import com.bank.integra.general.repository.RolesRepository;
import com.bank.integra.user.repository.UserRepository;
import com.bank.integra.general.enums.EmailValidationResponse;
import com.bank.integra.admin.dto.AdminDTO;
import com.bank.integra.admin.service.AdminPersistUserService;
import com.bank.integra.user.service.UserService;
import com.bank.integra.general.validation.EmailValidator;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
public class AdminDTOController {
    private final UserRepository userRepository;
    private final RolesRepository rolesRepository;
    private final AdminPersistUserService persistUserService;
    private final UserService userService;
    private final EmailValidator emailValidator;

    public AdminDTOController(UserService userService, AdminPersistUserService persistUserService,
                              RolesRepository rolesRepository, UserRepository userRepository, EmailValidator emailValidator) {
        this.userRepository = userRepository;
        this.rolesRepository = rolesRepository;
        this.persistUserService = persistUserService;
        this.userService = userService;
        this.emailValidator = emailValidator;
    }

    //TODO Add Validation
    @PostMapping("/save-user")
    public String processForm(@Valid @ModelAttribute AdminDTO adminDTO, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        if(bindingResult.hasErrors()) {
            return "redirect:/admin/home";
        }
        EmailValidationResponse response = emailValidator.checkEmail(adminDTO.getEmail(), adminDTO.getUserId());
        if(response.isSuccess()) {
            persistUserService.saveUserFromForm(adminDTO, model);
            return "result";
        } else {
            redirectAttributes.addFlashAttribute("information", response.getDescription());
            return "redirect:/admin/home";
        }

    }
}
