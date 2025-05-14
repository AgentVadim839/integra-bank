package com.bank.integra.services.person;

import com.bank.integra.dao.TransactionsRepository;
import com.bank.integra.dao.UserDetailsRepository;
import com.bank.integra.entities.details.Transaction;
import com.bank.integra.entities.details.UserDetails;
import com.bank.integra.services.customTools.OlegList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TransactionsService {

    @Autowired
    private TransactionsRepository transactionRepository;
    @Autowired
    private UserDetailsRepository userDetailsRepository;
    @Autowired
    private UserService userService;

    // TODO Говно чисть, которое ты кодом назвал (хотя оно работает):
    // [🔥 срочно починить откаты транзакции, когда sender или recipient не найдены]:
    // В методе saveTransaction() кидаются RuntimeException'ы, если не найден sender или recipient.
    // Эти исключения внутри транзакции помечают её как rollback-only, и даже если ты их отловишь,
    // Spring всё равно откатит БД. В итоге в контроллере будет UnexpectedRollbackException,
    // и ты такой сидишь и не понимаешь, в чём дело 🤯
    //
    // 👉 Нужно:
    // 1. Вместо .orElseThrow(() -> new RuntimeException(...)) — использовать orElse(null)
    // 2. Потом вручную проверить на null и выбросить IllegalArgumentException или вернуть null.
    //    Это НЕ приведёт к автоматическому rollback'у, и контроллер сможет обработать ошибку.
    // 3. Контроллер должен отловить эту ошибку и не пугаться, что транзакция вон уже вся откатилась.
    //
    // 🎯 Цель: чтобы метод createAndSave() не взрывал транзакцию по пустякам.
    // 🧠 Подумать: может, вообще делать проверку на существование пользователей ДО входа в @Transactional?


    public Transaction saveTransaction(Transaction transaction) {
        UserDetails sender = userDetailsRepository.findById(transaction.getSender().getUserId()).orElse(null);
        UserDetails recipient = userDetailsRepository.findById(transaction.getRecipient().getUserId()).orElse(null);

        return transactionRepository.save(transaction);
    }

    public Transaction createTransaction(Integer senderId, Integer recipientId, Double balance, String description, UUID idempotencyKey) {
        Transaction transaction = new Transaction();
        transaction.setSender(userService.getUserDetailsByUserId(senderId));
        transaction.setRecipient(userService.getUserDetailsByUserId(recipientId));
        transaction.setBalance(balance);
        transaction.setEventTimeStamp(LocalDateTime.now());
        transaction.setDescription(description);
        transaction.setIdempotencyKey(idempotencyKey.toString());
        return transaction;
    }

    public Transaction createAndSave(Integer senderId, Integer recipientId, Double balance, String description, UUID idempotencyKey) {
        Transaction transaction = createTransaction(senderId, recipientId, balance, description, idempotencyKey);
        return saveTransaction(transaction);
    }

    public List<Transaction> getSentTransactions(Integer senderId) {
        return transactionRepository.findBySender(userService.getUserDetailsByUserId(senderId));
    }

    public List<Map<String, Object>> getFormattedTransactionsForUser(Integer userId) {
        UserDetails user = userDetailsRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Transaction> allTransactions = transactionRepository.findAll();
        List<Map<String, Object>> result = new OlegList();

        for (Transaction t : allTransactions) {
            if (t.getSender().equals(user) || t.getRecipient().equals(user)) {
                Map<String, Object> map = new HashMap<>();
                if (t.getSender().equals(user)) {
                    map.put("type", "SENT");
                    map.put("to", t.getRecipient().getFirstName() + " " + t.getRecipient().getLastName());
                    map.put("from", t.getSender().getFirstName() + " " + t.getSender().getLastName());
                } else {
                    map.put("type", "RECEIVED");
                    map.put("to", t.getRecipient().getFirstName() + " " + t.getRecipient().getLastName());
                    map.put("from", t.getSender().getFirstName() + " " + t.getSender().getLastName());
                }

                map.put("amount", t.getBalance());
                map.put("timestamp", t.getEventTimeStamp());
                map.put("description", t.getDescription());

                result.add(map);
            }
        }

        return result;
    }



    public List<Transaction> getReceivedTransactions(Integer recipientId) {
        return transactionRepository.findByRecipient(userService.getUserDetailsByUserId(recipientId));
    }
}

