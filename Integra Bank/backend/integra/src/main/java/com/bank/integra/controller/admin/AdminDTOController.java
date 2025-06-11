package com.bank.integra.controller.admin;


import com.bank.integra.dao.RolesRepository;
import com.bank.integra.dao.UserDetailsRepository;
import com.bank.integra.dao.UserRepository;
import com.bank.integra.entities.details.UserDetails;
import com.bank.integra.entities.person.User;
import com.bank.integra.entities.role.Role;
import com.bank.integra.enums.EmailValidationResponse;
import com.bank.integra.services.DTO.AdminDTO;
import com.bank.integra.services.admin.AdminPersistUserService;
import com.bank.integra.services.person.UserService;
import com.bank.integra.services.validation.EmailValidation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RolesRepository rolesRepository;

    @Autowired
    private AdminPersistUserService persistUserService;

    @Autowired
    private UserService userService;

    //TODO хуйня, валидацию тоже надо
    @PostMapping("/save-user")
    public String processForm(@Valid @ModelAttribute AdminDTO adminDTO, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        if(bindingResult.hasErrors()) {
            return "redirect:/admin/home";
        }
        EmailValidationResponse response = EmailValidation.checkEmail(adminDTO.getEmail(), adminDTO.getUserId(), userService);
        if(response.isSuccess()) {
            persistUserService.saveUserFromForm(adminDTO, model);
            return "result";
        } else {
            redirectAttributes.addFlashAttribute("information", response.getDescription());
            return "redirect:/admin/home";
        }

    }
}
