package com.petdb.transaction;

import com.petdb.parser.query.Keyword;

import java.time.LocalDateTime;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.Optional;

import static com.petdb.util.EngineUtil.DATE_TIME_FORMATTER;

public final class TransactionHandler {

    private final Deque<Transaction> transactions = new ArrayDeque<>();

    public String begin() {
        var startTimeStamp = LocalDateTime.now().format(DATE_TIME_FORMATTER);
        var transaction = new Transaction(startTimeStamp);
        this.transactions.push(transaction);
        return String.format(Keyword.BEGIN + ": %s : %s", transaction.getUuid(), transaction.getTimestamp());
    }

    public String rollback() {
        var optional = this.peekOptional();
        if (optional.isPresent()) {
            var transaction = optional.get();
            transaction.getMap().clear();
            return String.format(Keyword.ROLLBACK + ": %s", transaction.getUuid());
        } else {
            return "No active transaction[s]";
        }
    }

    public Optional<Transaction> commit() {
        return this.peekOptional().isPresent() ? Optional.of(this.transactions.pop()) : Optional.empty();
    }

    public String end() {
        if (this.peekOptional().isPresent()) {
            var transaction = this.transactions.pop();
            return String.format(Keyword.END + ": %s", transaction.getUuid());
        } else {
            return "No active transaction[s]";
        }
    }

    public String set(String key, String value) {
        var map = this.getMapFromActiveTransaction();
        map.put(key, value);
        return "OK";
    }

    public String get(String key) {
        var map = this.getMapFromActiveTransaction();
        var value = map.get(key);
        if (value == null) {
            return String.format("Key = %s, not set", key);
        }
        return value;
    }

    public String delete(String key) {
        var map = this.getMapFromActiveTransaction();
        map.remove(key);
        return Keyword.DELETE.toString();
    }

    public boolean isActive() {
        return !this.transactions.isEmpty();
    }

    private Optional<Transaction> peekOptional() {
        return Optional.ofNullable(this.transactions.peek());
    }

    private Map<String, String> getMapFromActiveTransaction() {
        return isActive() ? this.transactions.peek().getMap() : null;
    }

    public String count() {
        var count = this.getMapFromActiveTransaction().size();
        return String.valueOf(count);
    }
}
