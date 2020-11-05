package com.petdb.transaction;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Locale;
import java.util.Optional;

// TODO better output.
public class TransactionHandler {

    private final static DateTimeFormatter DATE_TIME_FORMATTER = new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .append(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            .toFormatter(Locale.ENGLISH);

    private final Deque<Transaction> stack = new ArrayDeque<>();

    public String begin() {
        var startTimeStamp = LocalDateTime.now().format(DATE_TIME_FORMATTER);
        var transaction = new Transaction(startTimeStamp);
        this.stack.push(transaction);
        return "transaction begin";
    }

    public String rollback() {
        var optional = this.peek();
        if (optional.isPresent()) {
            var transaction = optional.get();
            transaction.getLocalStore().clear();
            return "rollback";
        } else {
            return "no active transaction";
        }
    }

    public Optional<Transaction> commit() {
        return this.peek().isPresent() ? Optional.of(this.stack.pop()) : Optional.empty();
    }

    public String end() {
        if (this.peek().isPresent()) {
            var transaction = this.stack.pop();
            return "transaction end";
        } else {
            return "no active transaction";
        }
    }

    public Optional<Transaction> peek() {
        return Optional.ofNullable(this.stack.peek());
    }

    public String delete(String key, Transaction transaction) {
        transaction.getLocalStore().remove(key);
        return "delete";
    }

    public String set(String key, String value, Transaction transaction) {
        transaction.getLocalStore().put(key, value);
        return "OK";
    }

    public String get(String key, Transaction transaction) {
        return transaction.getLocalStore().get(key);
    }
}
