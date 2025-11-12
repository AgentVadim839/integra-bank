package com.bank.integra.user.controller;

import com.bank.integra.user.model.UserDetails;
import com.bank.integra.general.enums.EmailValidationResponse;
import com.bank.integra.general.api.CurrencyService;
import com.bank.integra.async.AsyncManager;
import com.bank.integra.transaction.service.TransactionsService;
import com.bank.integra.user.service.UserService;
import com.bank.integra.general.validation.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;

@RequestMapping("/user")
@Controller
public class DashboardController {
    private final UserService userService;
    private final TransactionsService transactionsService;
    private final AsyncManager asyncManager;
    private final CurrencyService currencyService;
    private final EmailValidator emailValidator;

    public DashboardController(UserService userService, TransactionsService transactionsService, AsyncManager asyncManager, CurrencyService currencyService, EmailValidator emailValidator) {
        this.userService = userService;
        this.transactionsService = transactionsService;
        this.asyncManager = asyncManager;
        this.currencyService = currencyService;
        this.emailValidator = emailValidator;
    }

    @GetMapping("/home")
    public String showMainPage(Authentication authentication, Model model) {
        Integer userId = Integer.parseInt(authentication.getName());
        UserDetails userDetails = userService.getUserDetailsByUserId(userId);

        List<Map<String, Object>> threeRecentTransactions = transactionsService.getFormattedTransactionsForUserThreeRecent(userId);

        //For fun
        String displayedUserBalance;
        double userBalance = userDetails.getBalance();
        double zazillion = 1_000_000_000_000_000.0;
        if(userBalance >= zazillion) {
            String numZaz = userBalance + "";
            displayedUserBalance = numZaz.charAt(0) + " ZAZILLION DOLLAS 🤑🤑";
        } else {
            displayedUserBalance = String.format("$%,.2f", userBalance);
        }

        // Получаем курс USD → UAH и блокируем (в контроллере допустимо)
        Map<String, Map<String, String>> usdRate = currencyService.getUsdExchangeRate();

        model.addAttribute("user", userDetails);
        model.addAttribute("balance", displayedUserBalance);
        model.addAttribute("usdToUah", usdRate.get("USD"));
        model.addAttribute("transactions", threeRecentTransactions);
        return "dashboard";
    }

    @GetMapping("/all-transactions")
    public String showTransactionsPage() {
        return "allTransactions";
    }

    @GetMapping("/transactions/load")
    @ResponseBody
    public List<Map<String, Object>> loadTransactions(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        Integer userId = Integer.parseInt(authentication.getName());
        return transactionsService.getFormattedTransactionsForUser(userId, page, size);
    }

    @GetMapping("/settings")
    public String showSettings(Authentication authentication, Model model) {
        Integer userId = Integer.parseInt(authentication.getName());
        UserDetails user = userService.getUserDetailsByUserId(userId);
        model.addAttribute("user", user);
        return "settings";
    }

    @PostMapping("/change-password")
    public String changePassword(Authentication authentication, RedirectAttributes redirectAttributes) {
        Integer userId = Integer.parseInt(authentication.getName());
        UserDetails user = userService.getUserDetailsByUserId(userId);
        String email = user.getEmail();
        EmailValidationResponse response = emailValidator.checkEmail(email, userId);
        if(response.isSuccess() || response == EmailValidationResponse.EMAIL_IS_SAME_AS_CURRENT) {
            asyncManager.runEmailSendTask(email);
            redirectAttributes.addFlashAttribute("information", "A confirmation email has been sent to your inbox.");
        } else {
            redirectAttributes.addFlashAttribute("information", response.getDescription());
        }
        return "redirect:/user/settings";
    }

    @PostMapping("/change-email")
    public String changeEmail(@RequestParam String newEmail, Authentication authentication, RedirectAttributes redirectAttributes) {
        Integer userId = Integer.parseInt(authentication.getName());
        EmailValidationResponse response = emailValidator.checkEmail(newEmail, userId);
        if(response.isSuccess()) {
            UserDetails user = userService.getUserDetailsByUserId(userId);
            user.setEmail(newEmail);
            userService.updateUserDetails(user);
            redirectAttributes.addFlashAttribute("information", "Email was changed successfully.");
        } else {
            redirectAttributes.addFlashAttribute("information", response.getDescription());
        }
        return "redirect:/user/settings";
    }

    @GetMapping("/")
    public String showBase() {
        return "redirect:/home";
    }
}