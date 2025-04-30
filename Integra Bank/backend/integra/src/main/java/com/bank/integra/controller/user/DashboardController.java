package com.bank.integra.controller.user;

import com.bank.integra.entities.details.UserDetails;
import com.bank.integra.services.API.CurrencyService;
import com.bank.integra.services.person.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@RequestMapping("/user")
@Controller
public class DashboardController {
    @Autowired
    UserService userService;

    @Autowired
    CurrencyService currencyService;

    //TODO Сделать нормальную историю транзакций между пользователями, списком.
    @GetMapping("/home")
    public String showMainPage(Authentication authentication, Model model) {
        Integer userId = Integer.parseInt(authentication.getName());
        UserDetails userDetails = userService.getUserDetailsByUserId(userId);

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
        return "dashboard";
    }

    @GetMapping("/")
    public String showBase() {
        return "redirect:/home";
    }
}