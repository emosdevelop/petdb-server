package com.petdb.transaction;

import com.petdb.parser.query.Key;
import com.petdb.parser.query.Keyword;
import com.petdb.parser.query.Value;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.*;

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
        return this.peekOptional().isPresent() ? Optional.of(this.stack.pop()) : Optional.empty();
    }

    public String end() {
        if (this.peekOptional().isPresent()) {
            var transaction = this.stack.pop();
            return String.format(Keyword.END + ": %s", transaction.getUuid());
        } else {
            return "No active transaction[s]";
        }
    }

    public String set(Key key, Value value) {
        var map = this.getMapFromActiveTransaction();
        map.put(key, value);
        return "OK";
    }

    public String get(Key key) {
        var map = this.getMapFromActiveTransaction();
        var value = map.get(key);
        if (value == null) {
            return String.format("Key = %s, not set", key.getData());
        }
        return value.getData();
    }

    public String delete(Key key) {
        var map = this.getMapFromActiveTransaction();
        map.remove(key);
        return Keyword.DELETE.toString();
    }

    public boolean isActive() {
        return !this.stack.isEmpty();
    }

    private Optional<Transaction> peekOptional() {
        return Optional.ofNullable(this.stack.peek());
    }

    private Map<Key, Value> getMapFromActiveTransaction() {
        return isActive() ? this.stack.peek().getMap() : null;
    }

    public String count() {
        var count = this.getMapFromActiveTransaction().size();
        return String.valueOf(count);
    }
}
