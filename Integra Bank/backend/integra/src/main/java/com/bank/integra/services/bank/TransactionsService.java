package com.bank.integra.services.bank;

import com.bank.integra.dao.TransactionsRepository;
import com.bank.integra.dao.UserDetailsRepository;
import com.bank.integra.entities.details.Transaction;
import com.bank.integra.entities.details.UserDetails;
import com.bank.integra.services.customTools.OlegList;
import com.bank.integra.services.person.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

//TODO Написать тесты для транзекшнс хесторе
//TODO и сделать динамическое отображение недавних транзакций, даже если < 3
@Service
public class TransactionsService {

    @Autowired
    private TransactionsRepository transactionRepository;
    @Autowired
    private UserDetailsRepository userDetailsRepository;
    @Autowired
    private UserService userService;

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

    private List<Transaction> prepareLists(Integer userId, UserDetails user) {
        List<Transaction> filtered = transactionRepository.findAll().stream()
                .filter(t -> {
                    UserDetails sender = t.getSender();
                    UserDetails recipient = t.getRecipient();

                    // Проверяем, что отправитель не null и равен текущему пользователю
                    boolean isSender = (sender != null && sender.equals(user));

                    // Проверяем, что получатель не null и равен текущему пользователю
                    boolean isRecipient = (recipient != null && recipient.equals(user));

                    // Возвращаем true, если текущий пользователь является отправителем или получателем
                    // Соответственно если пользователь == null, тогда результатом будет false
                    return isSender || isRecipient;
                })
                .sorted() // тут работает Comparable<Transaction>
                .toList();
        return filtered;
    }

    private List<Map<String, Object>> formatLists(List<Transaction> filtered, UserDetails user) {
        List<Map<String, Object>> result = new OlegList();
        for (Transaction t : filtered) {
            Map<String, Object> map = new HashMap<>();
            UserDetails sender = t.getSender();
            UserDetails recipient = t.getRecipient();
            String senderName = (sender != null) ? sender.getFirstName() + " " + sender.getLastName()
                    : "User is deleted.";
            String recipientName = (recipient != null) ? recipient.getFirstName() + " " + recipient.getLastName()
                    : "User is deleted.";

            if (sender != null && sender.equals(user)) {
                map.put("type", "SENT");
            } else {
                map.put("type", "RECEIVED");
            }

            map.put("from", senderName);
            map.put("to", recipientName);
            map.put("amount", t.getBalance());
            map.put("timestamp", t.getEventTimeStamp());
            map.put("description", t.getDescription());

            result.add(map);
        }
        return result;
    }

    public List<Map<String, Object>> getFormattedTransactionsForUserThreeRecent(Integer userId) {
        UserDetails user = userDetailsRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<Transaction> filtered = prepareLists(userId, user);
        if(filtered.isEmpty()) {
            return List.of();
        } else {
            List<Transaction> recent = filtered.subList(0, Math.min(3, filtered.size()));
            return formatLists(recent, user);
        }
    }

    public List<Map<String, Object>> getFormattedTransactionsForUser(Integer userId, int page, int size) {
        UserDetails user = userDetailsRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<Transaction> filtered = prepareLists(userId, user);

        int fromIndex = page * size;
        int toIndex = Math.min(fromIndex + size, filtered.size());
        if(fromIndex > filtered.size()) return Collections.emptyList();

        List<Transaction> paged = filtered.subList(fromIndex, toIndex);
        List<Map<String, Object>> result = formatLists(paged, user);
        return result;
    }



    public Integer findTransactionIdByIdempotencyKey(String idempotencyKey) {
        Optional<Transaction> transactionOptional = transactionRepository.findByIdempotencyKey(idempotencyKey);
        return transactionOptional.map(Transaction::getId).orElse(null);
    }

    public Optional<Transaction> getTransactionById(Integer id) {
        return transactionRepository.findById(id);
    }

    public List<Transaction> getReceivedTransactions(Integer recipientId) {
        return transactionRepository.findByRecipient(userService.getUserDetailsByUserId(recipientId));
    }
}

