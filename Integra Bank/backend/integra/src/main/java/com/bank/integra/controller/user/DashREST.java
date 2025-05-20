package com.bank.integra.controller.user;

import com.bank.integra.entities.details.Transaction;
import com.bank.integra.entities.details.UserDetails;
import com.bank.integra.entities.person.User;
import com.bank.integra.services.person.TransactionsService;
import com.bank.integra.services.person.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class DashREST {
    @Autowired
    private TransactionsService transactionsService;

    @GetMapping("/transactions/{id}")
    public List<Map<String, Object>> showPrint(@PathVariable Integer id) {
        return transactionsService.getFormattedTransactionsForUserThreeRecent(id);
    }
}
